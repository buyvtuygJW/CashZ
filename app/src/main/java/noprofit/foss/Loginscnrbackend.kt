package noprofit.foss

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import noprofit.foss.NOSQL.NoSQLHelper
import javax.inject.Inject
//serialize&deserialize json
import com.google.gson.Gson

//for singleton
import javax.inject.Singleton
import noprofit.foss.NOSQL.OVERRIDEimportnosqlfromjsonstrwritteninarr

//for encryptedpref
object KeysinEncryptedPref {
    const val PASSWORD = "applockpass"
    const val NOSQLJSONDT = "transact_json_data"
}

/**
eg get>private val authViewModel: Loginscnrbackend.get() = loginscnrbackendSingleton.instance
*/
@Singleton
class LoginbackendSingleton @Inject constructor(private val noSQLHelper: NoSQLHelper) {
    private var instance: Loginscnrbackend? = null

    fun getInstance(): Loginscnrbackend {
        if (instance == null) {
            instance = Loginscnrbackend(noSQLHelper)
        }
        return instance!!
    }
}

@HiltViewModel
public class Loginscnrbackend @Inject public constructor( val nosqlhelpobj: NoSQLHelper): ViewModel() {

    // Step 1: Define state variables for username, password, loading state, and login error

    // StateFlow for login screen state
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Step 3: Handle changes to the password input field
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Step 4: Perform login operation.eg with username
    fun login(context: Context,`onLoginSuccess`: () -> Unit){
        _isLoading.value = true
        viewModelScope.launch {
            // Step 4.1: Perform authentication (replace with your actual authentication logic)
            val isSuccess = authenticate(context,password.value)
            _isLoading.value = false
            if (isSuccess) {
                _isLoggedIn.value = true
                // Debug log for checking the ViewModel instance and isLoggedIn state
                //android.util.Log.d("Loginbackend", "instance: $this")
                //android.util.Log.d("Login backend,success timing","isloggedin, $isLoggedIn")
                val jsondt = utils.EncryptedPref.getData(context, KeysinEncryptedPref.NOSQLJSONDT)

                if (utils.ismeaningfuljsondt(jsondt)){
                    OVERRIDEimportnosqlfromjsonstrwritteninarr(jsondt, nosqlhelpobj)
                // Add new data
                }else{android.widget.Toast.makeText(context,"Please import data~",android.widget.Toast.LENGTH_LONG).show()}
                // Step 4.2: Invoke the success callback if login is successful
                onLoginSuccess()
            }else{
                // Step 4.3: Update the login error message if login fails
                //save to encryption then logout
                saveencryptnosql(context)
                ForceLOGOUT()
                _loginError.value = "Login failed. Please try again."
            }
        }
    }

    //src,Trigger the save and encryption process when the activity goes to the background
    fun saveencryptnosql(context: Context){
        val gson = Gson()
        val jsonString = gson.toJson(nosqlhelpobj.transactionBox.all)
        utils.EncryptedPref.saveData(context,KeysinEncryptedPref.NOSQLJSONDT,jsonString)
    }

    public fun ForceLOGOUT(){
        //android.util.Log.d("AUTH,FORCE LOG OUT,","you know why?")
        _isLoggedIn.value = false
        //nosqlhelpobj.transactionBox.removeAll() // Clear existing data
    }

    // Step 5: Stub for authentication logic
    private suspend fun authenticate(context: Context,password: String): Boolean {
        //actual implementation.
        // Retrieving data
        val sysrealpass = utils.EncryptedPref.getData(context, noprofit.foss.KeysinEncryptedPref.PASSWORD)
        return password==sysrealpass
    }

}