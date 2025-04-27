package noprofit.foss.importsql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

//setp2,SQL Reading
class SQLDBHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "mytestdb.db"
        private var DATABASE_VERSION = 1

        @JvmStatic
        fun checkIfSQLiteFile(inputStream: InputStream): Pair<Boolean, InputStream> {
            val fileHeader = ByteArray(16)
            inputStream.read(fileHeader)
            val isSQLiteFile = String(fileHeader).startsWith("SQLite format 3")
            val newInputStream = ByteArrayInputStream(fileHeader + inputStream.readBytes()) // Create a new InputStream
            return Pair(isSQLiteFile, newInputStream)
        }

        @JvmStatic
        fun chckDBrequirements(context: Context): Boolean{
            val dbDir = context.getDatabasePath(DATABASE_NAME).parentFile
            if (dbDir != null && !dbDir.exists()) {
                val created = dbDir.mkdirs()
                if (created) {
                    Log.d("DatabaseFix", "Successfully created databases directory: ${dbDir.absolutePath}")
                } else {
                    Log.e("DatabaseFix", "Failed to create databases directory.")
                }
            }
            //Log.d("Database", "THE databases directory: ${dbDir.absolutePath}")//emergency fail create database debug use.
            return true
        }
    }

    /**
    // Initial database setup if needed
     */
    override fun onCreate(db: SQLiteDatabase){}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade
        Log.d("DatabaseHelper", "Upgrading database from version $oldVersion to $newVersion")
    }

    //use these 2 overwritten to custom db,stupid ai don;t know how to solve error until I hint overwrite rhe api
    override fun getWritableDatabase(): SQLiteDatabase {
        val databasePath = context.getDatabasePath(DATABASE_NAME).absolutePath
        val db = SQLiteDatabase.openOrCreateDatabase(databasePath, null)
        db.close()
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        //return super.getReadableDatabase()
        val databasePath = context.getDatabasePath(DATABASE_NAME).absolutePath
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    /**import
     * */
    fun executeSQLFile(sqlContent: String) {
        clearDatabase()
        val db = writableDatabase
        val reader = BufferedReader(InputStreamReader(sqlContent.byteInputStream()))
        val sql = StringBuilder()
        var line: String?
        try {
            db.beginTransaction()
            while (reader.readLine().also { line = it } != null) {
                sql.append(line)
                if (line!!.trim().endsWith(";")) {
                    //Log.d("DatabaseHelper", "Executing SQL: $sql")
                    db.execSQL(sql.toString())
                    sql.setLength(0)
                }
            }
            db.setTransactionSuccessful()
            db.close()
            //listTables()//debug use
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error executing SQL file", e)
        } finally {
            db.endTransaction()
            reader.close()
        }
    }

    fun copySQLiteFile(inputStream: InputStream) {
        //clearDatabase()// nah because we directly copy in
        val outputFileName = context.getDatabasePath(DATABASE_NAME).absolutePath
        try {
            // Copy the SQLite file
            inputStream.use { input ->
                context.getDatabasePath(DATABASE_NAME).outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Determine the version of the imported database
            val importedDbVersion = getImportedDatabaseVersion()
            //Log.d("DatabaseHelper", "Imported database version: $importedDbVersion")

            // Set the database version to match the imported file
            setDatabaseVersionv3(importedDbVersion)

            // Manually refresh database connection
            close()
            val db = SQLiteDatabase.openDatabase(outputFileName, null, SQLiteDatabase.OPEN_READWRITE)
            //Log.d("DatabaseHelper", "Database version after copying: ${db.version}")
            db.close()
            //listTables()//debug use
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error copying SQLite file", e)
        }
    }

    public fun getImportedDatabaseVersion(): Int {
        var importedDbVersion = DATABASE_VERSION
        try {
            val db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
            importedDbVersion = db.version
            db.close()
        } catch (e: Exception) {
            //Log.e("DatabaseHelper", "Error getting imported database version", e)
        }
        return importedDbVersion
    }

    private fun setDatabaseVersionv3(version: Int){
        try {
            val db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
            //Log.d("SQLHelper", "Setting database version to $version")
            db.execSQL("PRAGMA user_version = $version")
            db.close()
            //Log.d("SQLHelper", "Database version set to $version")
        } catch (e: Exception) {
            Log.e("SQLHelper", "Error setting database version", e)
        }
    }

    fun getTableData(tableName: String): Cursor? {
        val db = readableDatabase
        return try {
            db.rawQuery("SELECT * FROM $tableName", null)
        } catch (e: Exception) {
            Log.e("SQLHelper", "Error fetching table data", e)
            null
        }finally {
            db.close()
        }
    }

    fun listTables(): List<String> {
        val tables = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    tables.add(it.getString(0))
                } while (it.moveToNext())
            }
        }
        Log.d("SQLHelper", "Tables: $tables")
        return tables
    }

    //Step 3:Identify Database Type(cashew or actual)
    fun getFieldNames(tableName: String): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        val fieldNames = mutableListOf<String>()
        while (cursor.moveToNext()) {
            fieldNames.add(cursor.getString(1)) // Column name is at index 1
        }
        cursor.close()
        return fieldNames
    }

    fun clearDatabase() {
        val db = writableDatabase
        try {
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            val tables = mutableListOf<String>()

            if (cursor.moveToFirst()){
                do {
                    val tableName = cursor.getString(0)
                    if (tableName != "android_metadata" && tableName != "sqlite_sequence") {
                        tables.add(tableName)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()

            // Drop all tables
            tables.forEach { table ->
                db.execSQL("DROP TABLE IF EXISTS $table")
            }
        } catch (e: Exception) {
            Log.e("SQLHelper", "Error clearing database", e)
        }finally {
            db.close()
        }
    }

    fun clearDatabasev2() {
        val db = writableDatabase
        try {
            db.beginTransaction()
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            val tables = mutableListOf<String>()

            if (cursor.moveToFirst()){
                do {
                    val tableName = cursor.getString(0)
                    if (tableName != "android_metadata" && tableName != "sqlite_sequence") {
                        tables.add(tableName)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
            // Drop all tables
            tables.forEach { table ->
                db.execSQL("DROP TABLE IF EXISTS $table")
            }
            db.setTransactionSuccessful()
            db.endTransaction()
        } catch (e: Exception) {
            Log.e("SQLHelper", "Error clearing database", e)
        }finally {
            db.close()
        }
    }

    fun exportDatabasev1(uri: Uri) {
        var db = writableDatabase
        // Close the database
        db.close()

        // Path to the current database
        val dbFile = File(context.getDatabasePath(DATABASE_NAME).toString())

        // Copy the database file
        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(dbFile).use { input ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                    //println("Database exported to: $uri")//debug use.
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Reopen the database
        db = SQLiteDatabase.openDatabase(context.getDatabasePath(DATABASE_NAME).absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
    }

}