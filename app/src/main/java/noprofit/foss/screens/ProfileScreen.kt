package noprofit.foss.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import noprofit.foss.ui.theme.NavigationBarMediumTheme

// Step 1: UI & Screens Setup
@Composable
fun ProfileScreen(navController: NavController) {
    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            )  {
                Box(
                    modifier = Modifier
                        //.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp)
                        //.height(500.dp)// if have height will fix height and button below will o snap after extract
                        .clip(MaterialTheme.shapes.large)
                ) {
                    foss.utils.ExpandableSection(title = "Support developer") {
                        //Text(text = "Expanded Content for Section 1", fontSize = 16.sp) Spacer(modifier = Modifier.height(8.dp))
                        foss.utils.OpenUrlButton("Donate any amount","https://www.paypal.com/ncp/payment/V99Y4ME5K3LL4",Modifier.padding(5.dp).fillMaxWidth())
                        Spacer(modifier = Modifier.height(5.dp))
                        foss.utils.OpenUrlButton("Big donation(5 Pound)","https://www.paypal.com/ncp/payment/TXNS92R4GTH4E",Modifier.padding(5.dp).fillMaxWidth())
                    }
                }
//                Spacer(modifier = Modifier.height(5.dp))
                foss.utils.OpenUrlButton("Report Issues","https://github.com/buyvtuygJW/CashZ/issues",Modifier.padding(5.dp).fillMaxWidth())



            }
        }
    }
}