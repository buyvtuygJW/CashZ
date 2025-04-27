package noprofit.foss

//these icons cannot use mat3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**Here is step 2 for nav bar layout. Setup all route & each nav icon details
 * Next step is to setup a 'handler' , can be in same script but suggest not to may confuse. Refer HandleBottomavBar.kt
 * */

//initializing the data class with default parameters,code position irrelevant.Const type
data class Bottomnavitem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
)

//function to get the list of bottomNavigationItems,object code position relevant.Const type.
fun BottomNavItems() : List<Bottomnavitem> {
    return listOf(
        Bottomnavitem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = Screens.Home.route
        ),
        Bottomnavitem(
            label = "Statistic",
            icon = foss.utils.autosvgcomposeui.uiiconsoutlined.Statssvgrepo,
            //icon = Icons.Filled.Search,
            route = Screens.Statistic.route
        ),
        Bottomnavitem(
            label = "Profile",
            icon = Icons.Filled.AccountCircle,
            route = Screens.Profile.route
        ),
    )
}