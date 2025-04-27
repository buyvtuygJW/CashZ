package noprofit.foss.NOSQL

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.objectbox.Box
import io.objectbox.BoxStore
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type


fun <T> exportData(context: Context, boxStore: BoxStore, entityClass: Class<T>, fileName: String) {
        // Get the Box for the entity class
        val box: Box<T> = boxStore.boxFor(entityClass)
        
        // Retrieve all entities
        val entities = box.all
        
        // Convert entities to JSON
        val gson = Gson()
        val json = gson.toJson(entities)
        
        // Write JSON to file
        val file = File(context.filesDir, fileName)
        FileWriter(file).use {
            it.write(json)
        }
    }

    fun <T> importData(context: Context, boxStore: BoxStore, entityClass: Class<T>, fileName: String) {
        // Read JSON from file
        val file = File(context.filesDir, fileName)
        val json = FileReader(file).use { it.readText() }
        
        // Convert JSON to entities
        val gson = Gson()
        val listType: Type = TypeToken.getParameterized(List::class.java, entityClass).type
        val entities: List<T> = gson.fromJson(json, listType)
        
        // Save entities to ObjectBox
        val box: Box<T> = boxStore.boxFor(entityClass)
        box.put(entities)
    }

/**
 * This will just clear the current db SO MAKE SURE YOU SAVE IT BEFORE HAND if you need it or use other modified way to import/append data
 * The reason this is used not the direct 2 line gson.fromjson pass is because this data is in array.
 * */
fun OVERRIDEimportnosqlfromjsonstrwritteninarr(jsonstr: String?,nosqlhelpobj: NoSQLHelper){
    val gson = Gson()
    val transactionListType = object : TypeToken<MutableList<Transaction?>?>() {}.getType()

    val transactions: MutableList<Transaction> = gson.fromJson(jsonstr, transactionListType)
    //android.util.Log.d("Objectbox utils,load encryteddt","$transactions")//debug use
    nosqlhelpobj.clearDatabase()
    nosqlhelpobj.safeput(transactions)
}
