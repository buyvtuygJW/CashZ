package noprofit.foss.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import noprofit.foss.ui.theme.NavigationBarMediumTheme
//graph1,https://ehsannarmani.github.io/ComposeCharts/charts/pie-chart/
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import foss.utils.MultiToggleSelector
import noprofit.foss.NOSQL.NoSQLHelper


// Step 1: UI & Screens Setup.WARN the nosqlhelper is not injected,do not futher pass other than graph use
@Composable
fun StatisticUI(navController: NavController,noSQLHelper : NoSQLHelper) {
    var isPieChart by remember { mutableStateOf(true) }
    NavigationBarMediumTheme {
        Column (modifier = Modifier.padding(10.dp)){
            //dropdown to pick x axis

            var xaxisfieldname= "date_created"
            val dataMap=noSQLHelper.as2DDatapoints(axisXField = xaxisfieldname, axisYField = "amount")

            //v2,use a switch for utilizing max function in limited space.
            Text(text = if (isPieChart) "Pie Chart" else "Line Graph")

            Switch(
                checked = isPieChart,
                onCheckedChange = { isPieChart = it }
            )
            if (isPieChart) {
                Multitogglepiev1()
            } else {
                //android.util.Log.d("Stats screen,dt>","$dataMap")//debug use to confirm is valid data
                Gengraphwdaterange(dataMap,xaxisfieldname) // Replace with your Line Graph composable
            }
        }
    }
}


@Composable
fun Multitogglepiev1(){
    val chartOptions = listOf("Absolute Pie", "Inflow/Outflow Pie")
    var selectedChart by remember { mutableStateOf(chartOptions[0]) }
    var proportionalpie by remember { mutableStateOf(true) }    //later added

    val chartContent: Map<String, @Composable () -> Unit> = mapOf(
        "Absolute Pie" to {
            foss.utils.Customdatapiev2(LocalContext.current,groupByField = "note", aggregateField = "amount")  // Replace with your Pie Chart composable
             },
        "Inflow/Outflow Pie" to {
            Row {Switch(checked = proportionalpie, onCheckedChange = { proportionalpie = it });Text("Proportional")  }
            if(proportionalpie){
                foss.utils.Customdualdatapiev3(LocalContext.current,groupByField = "note", aggregateField = "amount")
            }else{
                foss.utils.Customdualdatapiev2(LocalContext.current,groupByField = "note", aggregateField = "amount")
            }
        }
    )

    HorizontalDivider(thickness = 4.dp)
    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chart Type", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        MultiToggleSelector(options = chartOptions,
            selectedOption = selectedChart,
            onOptionSelected = { selectedChart = it }
        )
        chartContent[selectedChart]?.invoke()
    }

}

@Composable
fun Gengraphwdaterange(dataMap: Map<String, Double>,xaxisfieldname: String){
    val context = LocalContext.current
    var isempty=dataMap.isEmpty()
    //android.util.Log.d("analyticscrn,data>","$dataMap $isempty")
    if (!dataMap.containsKey("Unknown")) {
        if (isempty) {
            Text("No data~ Please import sql")
            android.widget.Toast.makeText(
                context,
                "No data available to display",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } else {
            //test old graph only screen
            foss.utils.Composewidgets.GraphAnalyticsScreenv2(dataMap)
        }
    }else{
        Text("No data in $xaxisfieldname,unable to make graph")
        android.widget.Toast.makeText(
            context,
            "No data available to display",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}