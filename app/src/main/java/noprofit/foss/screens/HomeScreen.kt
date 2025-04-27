package noprofit.foss.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import noprofit.foss.NOSQL.NoSQLHelper
import noprofit.foss.ui.theme.NavigationBarMediumTheme

// Step 1: UI & Screens Setup
@Composable
fun HomeScreen(navController: NavController,noSQLHelper : NoSQLHelper) {
    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                noprofit.foss.EX.testedimportSQLUI_v3(noSQLHelper)

            }
        }
    }
}