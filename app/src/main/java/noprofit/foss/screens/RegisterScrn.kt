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

@Composable
fun RegisterScreen(vm: noprofit.foss.Registerbackend = viewModel(), onSuccess: () -> Unit) {
    val password by vm.password.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val loginError by vm.loginError.collectAsState()
    val contextforbtn = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){

         foss.utils.PasswordTextField(
            "Password to prevent others unlock",
            "example password",
            password,
            onChangePassword= { vm.onPasswordChange(it) },
             loginError,
        )


        Spacer(modifier = Modifier.height(10.dp))
        
        Button(
            onClick = { vm.setnewpass(contextforbtn,onSuccess) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {Text("Set password")}
        if (loginError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = loginError!!, color = MaterialTheme.colorScheme.error)
        }
    }
}