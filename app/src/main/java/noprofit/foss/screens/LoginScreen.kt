package noprofit.foss.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import noprofit.foss.Loginscnrbackend
//for inject hilt viewmodel not normal
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel


@Composable
//fun LoginScreen(loginViewModel: Loginscnrbackend = viewModel(), onLoginSuccess: () -> Unit) {//classic app view model
fun LoginScreen(loginViewModel: Loginscnrbackend, onLoginSuccess: () -> Unit) {//hilt inject way,MUST use this to create hilt style viewmodel else crash.
    val password by loginViewModel.password.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val loginError by loginViewModel.loginError.collectAsState()
    val contextforbtn = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){
        TextField(
            value = password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        Button(
            onClick = { loginViewModel.login(contextforbtn,onLoginSuccess) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {Text("Login")}
        if (loginError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = loginError!!, color = MaterialTheme.colorScheme.error)
        }
    }
}