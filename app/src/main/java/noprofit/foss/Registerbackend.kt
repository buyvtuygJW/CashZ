package noprofit.foss

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Registerbackend(): ViewModel()  {
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // Step 3: Handle changes to the password input field
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    // Step 4: Perform login operation.eg with username
    fun setnewpass(context: Context,onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            // Step 4.1: Save new pass
            //android.util.Log.d("new pass set","${password.value}")
            utils.EncryptedPref.saveData(context,noprofit.foss.KeysinEncryptedPref.PASSWORD,password.value)
            val isSuccess =true
            _isLoading.value = false
            if (isSuccess) {
                // Step 4.2: Invoke the success callback if login is successful
                onSuccess()
            } else {
                // Step 4.3: Update the login error message if login fails
                _loginError.value = "Login failed. Please try again."
            }
        }
    }
}