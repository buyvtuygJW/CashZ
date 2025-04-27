package noprofit.foss.NOSQL

import android.content.ContentValues
import android.content.Context
import android.util.Log
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import noprofit.foss.EX.DatabaseType
import noprofit.foss.EX.getdbtonosqlmapping
import noprofit.foss.importsql.SQLDBHelper

//for sql convert to list temp dt
import android.database.Cursor
import kotlin.Double
import kotlin.String

//edge case
import android.content.SharedPreferences
import java.util.UUID

//nosql to sql
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.toMutableStateMap
import com.google.gson.Gson
import noprofit.foss.EDGE.ActualdbgetCategoryNameFromTables
import noprofit.foss.EDGE.CashewgetCategoryNameFromBothTables
import noprofit.foss.EDGE.createcategorytblandinsertdtforcashewv2
import noprofit.foss.EDGE.dropexistingcashewtables
import noprofit.foss.EDGE.getcategorymatchedpkcashewv2
import noprofit.foss.EDGE.nosqltocashewsqlpatch
import noprofit.foss.KeysinEncryptedPref
import utils.EncryptedPref
import utils.customDateToUnixTimestamp
import utils.moneyasinttodouble
import utils.remapKeys
import kotlin.reflect.full.memberProperties

class NoSQLHelper(private val context: Context,private var boxStore: BoxStore ) {//w injection.
//class NoSQLHelper(private val context: Context) {//non inject old way.

    //private val boxStore: BoxStore = MyObjectBox.builder().androidContext(context).build()//non inject
    public var transactionBox: Box<Transaction> = boxStore.boxFor(Transaction::class.java)

    //for generate unique id tracking
    private val innerdt: SharedPreferences = context.getSharedPreferences("NoSQLHelper_EXTRAINFO", Context.MODE_PRIVATE)


    suspend fun convertFromSQLv2(sqlHelper: SQLDBHelper, databaseType: DatabaseType) {
        // Remove the last used ID from SharedPreferences
        innerdt.edit().remove("last_used_id").apply()
        clearDatabase()
        withContext(Dispatchers.IO) {
            try {
                val db = sqlHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM transactions", null)
                //Log.d("NOsql bf convert", "SQL Query Results: $cursor")//useless show obj name w no detail

                val mappings = getdbtonosqlmapping(databaseType)

                var sqlResults = convertCursorToList(cursor)
                //Log.d("NoSQLHelper,sql parsed dt", "$sqlResults")

                transactionBox.store.runInTx {
                    val noSQLItems = sqlResults.map { sqlItem ->
                        // Generate an ID if it's not present and the database type requires it
                        //val id = if (databaseType == DatabaseType.Cashew) {generateUniqueIdFromSQL(sqlHelper)//v1
                        //val id = generateUniqueIdFromSQL(sqlHelper)//v2 generate for both db
                        //sqlItem["id"] as? Long ?: 0L//prevent null
                        var sqluuid=""
                        if(databaseType == DatabaseType.ACTUAL){sqluuid = sqlItem["id"]?.toString() ?: UUID.randomUUID().toString()}
                        if(databaseType == DatabaseType.CASHEW){sqluuid = sqlItem["transaction_pk"]?.toString() ?: UUID.randomUUID().toString()}

                        val id = 0L//objectbox will auto increment.

                        var mappedData= remapKeys(sqlItem,mappings)

                        //Log.d("NoSQLHelper,input sql item", "$sqlItem")//debug use

                        if(databaseType == DatabaseType.ACTUAL){
                            mappedData["amount"]= moneyasinttodouble(mappedData["amount"])
                            mappedData["date_created"]= customDateToUnixTimestamp(mappedData["date_created"])
                        }//to patch the amount using long then check null.

                        //category patch
                        if(databaseType == DatabaseType.CASHEW){
                            mappedData["note"]= CashewgetCategoryNameFromBothTables(sqlHelper.writableDatabase,"transactions","category_fk","categories","category_pk")
                        }else if(databaseType == DatabaseType.ACTUAL){
                            mappedData["note"]= ActualdbgetCategoryNameFromTables(sqlHelper.writableDatabase,"transactions","category","categories","id",sqluuid)
                        }

                        //force no null for must have field
                        if (mappedData["name"] == null) { mappedData.put("name", "ANON") }
                        if (mappedData["amount"] == null) { mappedData.put("amount", 0.0) }
                        if (mappedData["date_created"] == null) { mappedData.put("date_created", 1L) }
                        if (mappedData["paid"] == null) { mappedData.put("paid", 0L) }

                        //Log.d("NoSQLHelper,mapped dt", "$mappedData")
                        //Log.d("NoSQLHelper,id ", "$id")//debug use
                        // Create a DatabaseItem with flat data
                        Transaction(
                            id = id,
                            name=mappedData["name"] as String,
                            amount=mappedData["amount"] as Double,//here has error,click line
                            note=mappedData["note"] as? String,
                            date_created = mappedData["date_created"] as Long,
                            date_time_modified = mappedData["date_time_modified"] as? Long,
                            original_date_due = mappedData["original_date_due"] as? Long,
                            income = mappedData["income"] as? Long,
                            paid = mappedData["paid"] as Long,
                            skipPaid = mappedData["skip_paid"] as? Long,
                            uuid = sqluuid ,// Nullable UUID
                        )
                    }
                    cursor.close()
                    Log.d("NoSQLHelper,final sql item", "$noSQLItems")
                    transactionBox.put(noSQLItems)
                    //Log.d("NoSQLHelper", "Conversion completed successfully")
                }
            } catch (e: Exception) {
                Log.e("NoSQLHelper", "Error processing file", e)
            }finally {
                // Trigger the save and encryption process when the activity goes to the background
                val gson = Gson()
                val jsonString = gson.toJson(transactionBox.all)
                EncryptedPref.saveData(context, KeysinEncryptedPref.NOSQLJSONDT,jsonString)
            }
        }
    }

    //function that help convert sql
    suspend fun convertCursorToList(cursor: Cursor): List<Map<String, Any?>> {
        val columnNames = cursor.columnNames
        val resultList = mutableListOf<Map<String, Any?>>()

        while (cursor.moveToNext()) {
            val rowMap = mutableMapOf<String, Any?>()
            for (columnName in columnNames) {
                val columnIndex = cursor.getColumnIndex(columnName)
                val value = when (cursor.getType(columnIndex)) {
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(columnIndex)
                    Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(columnIndex)
                    Cursor.FIELD_TYPE_STRING -> cursor.getString(columnIndex)
                    Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(columnIndex)
                    Cursor.FIELD_TYPE_NULL -> null
                    else -> null
                }
                rowMap[columnName] = value
            }
            resultList.add(rowMap)
        }
        cursor.close()
        return resultList
    }

    suspend fun getTransactionsJson(asString: Boolean = false): Any = withContext(Dispatchers.IO) {
                val transactions = transactionBox.all
                val jsonArray = JSONArray()
                transactions.forEach { transaction ->
                    val jsonObject = JSONObject()
                    jsonObject.put("id", transaction.id)
                    jsonObject.put("name", transaction.name)
                    jsonObject.put("amount", transaction.amount)
                    jsonObject.put("note", transaction.note)
                    jsonObject.put("date_created", transaction.date_created / 1000)
                    jsonObject.put("date_time_modified", transaction.date_time_modified?.div(1000) )
                    jsonObject.put("original_date_due", transaction.original_date_due?.div(1000))//error>jsonObject.put("originalDateDue", transaction.originalDateDue / 1000)
                    jsonObject.put("income", transaction.income)
                    jsonObject.put("paid", transaction.paid)
                    jsonObject.put("skipPaid", transaction.skipPaid)
                    jsonObject.put("uuid", transaction.uuid)
                    jsonArray.put(jsonObject)
                }
                return@withContext if (asString) jsonArray.toString() else jsonArray
    }

    //only act as example how to add.
    suspend fun addItem(databaseItem: Transaction) {
            transactionBox.put(databaseItem)
        }


    /**Using this function to put NEVER WILL HAPPEN>java.lang.IllegalArgumentException: ID is higher or equal to internal ID sequence: 4 (vs. 1). Use ID 0 (zero) to insert new objects.
     * */
    fun safeput(dbitems: List<Transaction>){
        dbitems.forEach { it.id = 0 }
        transactionBox.put(dbitems)
    }

    fun getAllItems(): List<Transaction> {
        return transactionBox.all
    }

    //fun clearDatabase() = withContext(Dispatchers.IO) {
    fun clearDatabase(){
        transactionBox.removeAll()
        //Log.d("NoSQLHelper", "ObjectBox database cleared successfully. ${transactionBox.all}")//debug use
    }

    fun as2DData(groupByField: String, aggregateField: String): Map<String, Double> {
        val items=getAllItems()
        val dataMap = mutableMapOf<String, Double>()

        items.forEach { item ->
            val groupByValue = when (groupByField) {
                //"uuid" -> item.uuid ?: "Unknown"//SHOULD NEVER USE THIS LMAO.unless they want to see every each transaction.
                "name" -> item.name
                "note" -> item.note ?: "Unknown"
                "date_created" -> item.date_created
                "date_time_modified" -> item.date_time_modified ?: "Unknown"
                "original_date_due" -> item.original_date_due ?: "Unknown"
                else -> "Unknown"
            }.toString()  // Ensure groupByValue is a String

            val aggregateValue = when (aggregateField) {
                //"income" -> item.income?.toDouble() ?: 0.0//should not use this as this is a boolean just to fit the cashew logic
                "amount" -> item.amount.toDouble()
                else -> 0.0
            }
            dataMap[groupByValue] = dataMap.getOrDefault(groupByValue, 0.0) + aggregateValue
        }
        return dataMap
    }

    fun as2DDatav2(groupByField: String, aggregateField: String): Map<String, Double> {
        val items=getAllItems()
        val dataMap = mutableMapOf<String, Double>()

        items.forEach { item ->
            val groupByValue = when (groupByField) {
                //"uuid" -> item.uuid ?: "Unknown"//SHOULD NEVER USE THIS LMAO.unless they want to see every each transaction.
                "name" -> item.name
                "note" -> item.note ?: "Unknown"
                "date_created" -> item.date_created
                "date_time_modified" -> item.date_time_modified ?: "Unknown"
                "original_date_due" -> item.original_date_due ?: "Unknown"
                else -> "Unknown"
            }.toString()  // Ensure groupByValue is a String

            val aggregateValue = when (aggregateField) {
                //"income" -> item.income?.toDouble() ?: 0.0//should not use this as this is a boolean just to fit the cashew logic
                "amount" -> item.amount.toDouble()
                else -> 0.0
            }
            //android.util.Log.d("NOSQL,as2ddtv2>","group by $groupByValue $item")
            dataMap[groupByValue] = dataMap.getOrDefault(groupByValue, 0.0) + aggregateValue
        }
        return dataMap
    }



    fun as2DDatapoints(axisXField: String, axisYField: String): Map<String, Double> {
        val items=getAllItems()
        val dataMap = mutableMapOf<String, Double>()

        items.forEach { item ->
            val groupByValue = when (axisXField) {
                //"uuid" -> item.uuid ?: "Unknown"//SHOULD NEVER USE THIS LMAO.unless they want to see every each transaction.
                "name" -> item.name
                "note" -> item.note ?: "Unknown"
                "date_created" -> item.date_created
                "date_time_modified" -> item.date_time_modified ?: "Unknown"
                "original_date_due" -> item.original_date_due ?: "Unknown"
                else -> "Unknown"
            }.toString()  // Ensure groupByValue is a String

            val aggregateValue = when (axisYField) {
                //"income" -> item.income?.toDouble() ?: 0.0//should not use this as this is a boolean just to fit the cashew logic
                "amount" -> item.amount.toDouble()
                else -> 0.0
            }
            dataMap[groupByValue] = dataMap.getOrDefault(groupByValue, 0.0) + aggregateValue
        }
        return dataMap
    }

    fun insertSQLItemv2(db: SQLiteDatabase, values: ContentValues){
        try {
            println(values)
            db.insert("transactions", null, values)
        } finally {
            //db.close()
        }
    }

    fun insertSQLItembeta(sqlHelper: SQLDBHelper, sqlItem:  MutableMap<String, String>) {
        val db = sqlHelper.writableDatabase
        val values = ContentValues()
        sqlItem.forEach { (field, value) ->
            values.put(field, value)
        }
        db.insert("transactions", null, values)
    }

    //try not using built in>sqlHelper: SQLDBHelper,
    //actual tested scripts.I know this script could be in better other p[ace too but bro lazy manage now.
    fun convertNoSQLToSQLv2(sqlHelper: SQLDBHelper, databaseType: DatabaseType) {
        // Clear the existing SQL database
        //sqlHelper.clearDatabase()//too weak
        sqlHelper.clearDatabasev2()
        //chckDBrequirements(context)
        //var sqlHelper =  SQLDBHelper(context.getApplicationContext())//try use brand new sqlhelper instead of clearing
        val db = sqlHelper.writableDatabase
        //withContext(Dispatchers.IO) {
                val noSQLItems = transactionBox.all

                val mappings = getdbtonosqlmapping(databaseType)
                // Reverse the map
                var reversedMap = mappings.map { (key, value) -> value to key }.toMutableStateMap()//now nosql to db map
                // Print the reversed map ,println(reversedMap)

        if(databaseType== DatabaseType.CASHEW){
            dropexistingcashewtables(db)
        }

            //sqlHelper.listTables()
            try {
                db.beginTransaction()
                //println(noSQLItems)//debug use.
                if (!isTableExists(db, "transactions")) {
                    if (databaseType== DatabaseType.ACTUAL){
                        createTransactionsTableforactual(db)
                    }
                    else if (databaseType== DatabaseType.CASHEW){
                        createTransactionsTableforcashewv1(db)
                    }
                }
                if (databaseType== DatabaseType.ACTUAL){reversedMap.put("uuid","id") }
                else if (databaseType== DatabaseType.CASHEW){reversedMap.put("uuid","transaction_pk")}//to patch the edge case of transaction_pk to uuid in sql to nosql,now reverse

                val existingColumns = getTableColumns(db, "transactions")
                var cleanednosqlitems = filterTransactions(noSQLItems,existingColumns,reversedMap)
                //android.util.Log.d("nosql dt,target db type>","$noSQLItems ,$databaseType")
                //android.util.Log.d("rev map>","$reversedMap")
                //android.util.Log.d("cleaned nosql dt>","$cleanednosqlitems")

                if(databaseType== DatabaseType.CASHEW){
                    createcategorytblandinsertdtforcashewv2(db)
                }//to prep layout

                cleanednosqlitems.forEach { filteredData ->
                    var contentValues = ContentValues().apply {
                        filteredData.forEach { (key, value) ->
                            when (value) {
                                is Int -> put(key, value)
                                is Long ->put(key, value)
                                is Double->put(key, value)
                                is Boolean->put(key, if (value) 1 else 0)
                                is String ->put(key, value)
                                else -> putNull(key)
                            }
                        }
                    }

                    if(databaseType== DatabaseType.CASHEW){contentValues= nosqltocashewsqlpatch(contentValues)
                        /**v1 patch
                         * var catid=getcategorymatchedpkcashew(contentValues,db)
                         *                         if (catid!=null){
                         *                             contentValues.put("category_fk",catid)
                         *                             contentValues.put("note","")//since we already now put the category_fk correctly we don't need note
                         *                         }
                         * */
                        var catid= getcategorymatchedpkcashewv2(contentValues,db)

                        contentValues.put("category_fk",catid)
                        contentValues.put("note","")//since we already now put the category_fk correctly we don't need note

                        contentValues.put("name","")//prevent long ass actual db crytic name
                    }
                    Log.d("final content value bf insert>","$contentValues")
                    val result = db.insert("transactions", null, contentValues)
                    if (result == -1L) { Log.e("DBHelper", "Error inserting data") }
                }

                db.setTransactionSuccessful()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()
                db.close()
            }

        //}
    }

    private fun createTransactionsTableforactual(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE "transactions" (
                "id"	TEXT,
                "isParent"	INTEGER DEFAULT 0,
                "isChild"	INTEGER DEFAULT 0,
                "acct"	TEXT,
                "category"	TEXT,
                "amount"	INTEGER, 
                "description"	TEXT,
                "notes"	TEXT,
                "date"	INTEGER,
                "financial_id"	TEXT,
                "type"	TEXT,
                "location"	TEXT,
                "error"	TEXT,
                "imported_description"	TEXT,
                "starting_balance_flag"	INTEGER DEFAULT 0,
                "transferred_id"	TEXT,
                "sort_order"	REAL,
                "tombstone"	INTEGER DEFAULT 0,
                "cleared"	INTEGER DEFAULT 1,
                "pending"	INTEGER DEFAULT 0,
                "parent_id"	TEXT,
                "schedule"	TEXT,
                "reconciled" INTEGER DEFAULT 0,
                PRIMARY KEY("id")
            );
        """
        //need remove>date_time_modified,income,
        db.execSQL(createTableQuery)
    }

    //without foreign key
    private fun createTransactionsTableforcashewv1(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE "transactions" (
                "transaction_pk"	TEXT NOT NULL,
                "paired_transaction_fk"	TEXT DEFAULT NULL,
                "name"	TEXT NOT NULL,
                "amount"	REAL NOT NULL,
                "note"	TEXT NOT NULL,
                "category_fk"	TEXT NOT NULL,
                "sub_category_fk"	TEXT DEFAULT NULL,
                "wallet_fk"	TEXT NOT NULL DEFAULT '0',
                "date_created"	INTEGER NOT NULL,
                "date_time_modified"	INTEGER DEFAULT 1731360779,
                "original_date_due"	INTEGER DEFAULT 1731360779,
                "income"	INTEGER NOT NULL DEFAULT 0 CHECK("income" IN (0, 1)),
                "period_length"	INTEGER,
                "reoccurrence"	INTEGER,
                "end_date"	INTEGER,
                "upcoming_transaction_notification"	INTEGER DEFAULT 1 CHECK("upcoming_transaction_notification" IN (0, 1)),
                "type"	INTEGER,
                "paid"	INTEGER NOT NULL DEFAULT 0 CHECK("paid" IN (0, 1)),
                "created_another_future_transaction"	INTEGER DEFAULT 0 CHECK("created_another_future_transaction" IN (0, 1)),
                "skip_paid"	INTEGER NOT NULL DEFAULT 0 CHECK("skip_paid" IN (0, 1)),
                "method_added"	INTEGER,
                "transaction_owner_email"	TEXT,
                "transaction_original_owner_email"	TEXT,
                "shared_key"	TEXT,
                "shared_old_key"	TEXT,
                "shared_status"	INTEGER,
                "shared_date_updated"	INTEGER,
                "shared_reference_budget_pk"	TEXT,
                "objective_fk"	TEXT,
                "objective_loan_fk"	TEXT,
                "budget_fks_exclude"	TEXT,
                PRIMARY KEY("transaction_pk")
            );
        """
        db.execSQL(createTableQuery)
    }

    private fun isTableExists(db: SQLiteDatabase, tableName: String): Boolean {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
        val exists = cursor.count > 0
        //println(exists)//debug use
        cursor.close()
        return exists
    }

    /**
     * Remove Invalid Pairs: The removeInvalidPairs function removes key-value pairs from ContentValues if the corresponding column does not exist in the table.
     * eg call api>
     * val existingColumns = getTableColumns(db, "transactions")
     *     val contentValues = convertTransactionToContentValues(transaction, noSQLToSQLMapping)
     *     removeInvalidPairs(contentValues, existingColumns)
     * */
    fun filterTransactions(transactions: List<Transaction>, existingColumns: Set<String>, mapping: Map<String, String>): List<Map<String, Any?>> {
        return transactions.map { transaction ->
            filterTransactionData(transaction, existingColumns, mapping)

        }
    }

    fun filterTransactionData(transaction: Transaction, existingColumns: Set<String>, mapping: Map<String, String>): Map<String, Any?> {
        val filteredData = mutableMapOf<String, Any?>()
        val kClass = transaction::class
        kClass.memberProperties.forEach { property ->
            val noSQLField = property.name
            val sqlField = mapping[noSQLField] ?: mapping.entries.find { it.value == noSQLField }?.key ?: noSQLField

            //println("Mapping: $noSQLField -> $sqlField (Exists in columns: ${sqlField in existingColumns})")

            if (sqlField in existingColumns) {
                val value = property.getter.call(transaction)
                filteredData[sqlField] = value
                //println("Mapped value: $sqlField = $value")
            }
        }
        return filteredData
    }

    fun getTableColumns(db: SQLiteDatabase, tableName: String): Set<String> {
        val columns = mutableSetOf<String>()
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        cursor.use {
            while (it.moveToNext()) {
                val columnName = it.getString(it.getColumnIndexOrThrow("name"))
                columns.add(columnName)
            }
        }
        return columns
    }

}