package noprofit.foss.EX

import android.app.Activity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.json.JSONArray

//temp test ui&Export
import utils.FilePickerHelper
import com.google.gson.Gson
import foss.utils.Composewidgets
import noprofit.foss.KeysinEncryptedPref
import noprofit.foss.NOSQL.NoSQLHelper
import noprofit.foss.importsql.SQLDBHelper
import utils.EncryptedPref
import utils.readFileContent
import utils.saveFile
/**
//This code isextremely dependant on app implementation
import noprofit.foss.EX.listofsupporteddb
import noprofit.foss.EX.DatabaseType
import noprofit.foss.EX.identifyDatabaseType
import noprofit.foss.EX.mapStringToEnum
*/

/**
//3 way to launch coroutine
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
*/


    //koroutine code
    //step1,ui for budget app.
    @Composable
    fun testedimportSQLUI_v3(noSQLHelperobj : NoSQLHelper) {//MUST GET THIS OBJECT PASSED FROM MAINACITIVTY TO HERE ELSE WILL CRASH.
        val context = LocalContext.current
        val databaseHelper = remember { SQLDBHelper(context) }
        var fileContent by remember { mutableStateOf<String?>(null) }
        var fileType by remember { mutableStateOf<String?>(null) }

        //prepare to save export sql function.(TESTED)
        val filePickerHelper = remember { FilePickerHelper(context) }
        var saveFileUri by remember { mutableStateOf<Uri?>(null) }
        val sqlexportlauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) { saveFileUri = result.data?.data }
        }

        //NOSQLhelper injected then remember it
        val noSQLhelpobj = remember { noSQLHelperobj }
        val coroutineScope = rememberCoroutineScope()//this need to be in top of function not inner of something.
        //var jsonTransactions by remember { mutableStateOf<JSONArray?>(null) }

        //for ui memory
        var selectedText =remember { mutableStateOf(listofsupporteddb().firstOrNull() ?: "Select an option") }

        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    fileType = context.contentResolver.getType(it)
                    val (isSQLiteFile, newInputStream) = SQLDBHelper.checkIfSQLiteFile(inputStream)
                    if (isSQLiteFile) {fileType = "application/.sqlite"}//if content has that header properly ignore any file subfix
                    coroutineScope.launch {
                        try {
                            if (fileType == "application/sql") {
                                val content = readFileContent(newInputStream)
                                fileContent = content
                                //android.util.Log.e("Home ui", "running sql?$content")//yea from here we know it is a full file for cashew,just bad subfix.
                                databaseHelper.executeSQLFile(content)
                            } else if (fileType == "application/vnd.sqlite3" || fileType == "application/.sqlite") {
                                databaseHelper.copySQLiteFile(newInputStream)
                            }
                            // Clear NoSQL database before conversion
                            noSQLhelpobj.clearDatabase()

                            // Step 3: Identify Database Type
                            val databaseType =
                                identifyDatabaseType(databaseHelper.getFieldNames("transactions"))
                            //android.util.Log.d("uieg>", "Identified Database Type: $databaseType")//tested WORKS up until here.

                            noSQLhelpobj.convertFromSQLv2(databaseHelper,databaseType)
                            //val jsonTransactions = noSQLhelpobj.getTransactionsJson() as JSONArray
                            //android.util.Log.d("homescrn,NOSQL out>","$jsonTransactions")
                        } catch (e: Exception) {
                            Log.e("app widget", "Error processing file", e)
                        }
                    }
                }
            }
        }


        Scaffold(
            topBar = {
                Text("SQL budget file conversion")
            },
            content = { paddingValues ->
                // Using Column to arrange multiple UI elements vertically
                Column (modifier = Modifier.padding(paddingValues)
                ) {
                    Composewidgets.ImportUIv4("Import",
                        onImportClick = { launcher.launch("*/*") },
                        paddingValues,
                        Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(5.dp))


                    Composewidgets.Companion.ReusableDropdown_v1overload("Export format",Modifier.weight(1.1f),listofsupporteddb(),"Save",selectedText){ selectedOption ->
                        val selecteddb= mapStringToEnum(selectedOption)
                        coroutineScope.launch {
                            //get dt from the written sql to file let user pick location.
                            var fileName: String=""
                            var mimetype: String="text/x-unknown"

                            val nonNullDatabaseType = requireNotNull(selecteddb) { "DatabaseType cannot be null" }
                            fileName = if (nonNullDatabaseType== DatabaseType.CASHEW){
                                "nosqltocashew.sql"//untested
                            }else if(nonNullDatabaseType== DatabaseType.ACTUAL){
                                "nosqltoActual.db"
                            }else{
                                "nosqlDT.json"
                            }

                            val intent = filePickerHelper.getFileSaveIntent(fileName,mimetype)
                            sqlexportlauncher.launch(intent)
                        }

                    }

                    //handle file export after select.
                    saveFileUri?.let {
                        val selecteddb= mapStringToEnum(selectedText.value)
                        // Ensure databaseType is non-null, throw an exception if it is null //way2 extra process for more type safety
                        //val nonNullDatabaseType = requireNotNull(selecteddb) { "DatabaseType cannot be null" }
                        if(selecteddb== DatabaseType.ACTUAL){
                            noSQLhelpobj.convertNoSQLToSQLv2(databaseHelper,selecteddb)
                            //convert to actual db then export...
                            databaseHelper.exportDatabasev1(it)
                        }else if(selecteddb== DatabaseType.CASHEW){
                            noSQLhelpobj.convertNoSQLToSQLv2(databaseHelper,selecteddb)
                            //convert to cashew db then export...
                            databaseHelper.exportDatabasev1(it)
                        }else if(selecteddb== DatabaseType.NOSQL){
                            //tested for nosql direct export.
                            val jsonstr = EncryptedPref.getData(context, KeysinEncryptedPref.NOSQLJSONDT)
                            jsonstr?.let{
                                nonnulljson->
                                saveFile(context.contentResolver, it,nonnulljson)
                            }?: run {
                                Toast.makeText(context,"Crashed at exporting as NOSQL. Please report to developer~",
                                    Toast.LENGTH_LONG).show()
                            }
                        }else if(selecteddb== DatabaseType.NOSQL_UNSAFERAW){
                            //tested for nosql direct export.
                            val gson = Gson()
                            val jsonString = gson.toJson(noSQLhelpobj.transactionBox.all)
                            saveFile(context.contentResolver, it,jsonString)
                        }
                    }

                }
            }
        )
    }

