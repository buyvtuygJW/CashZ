package noprofit.foss

import android.content.Context

sealed class Screens(val route : String) {
    data object Home : Screens("home_route")
    data object Statistic : Screens("statistic_route")
    object Profile : Screens("profile_route")
    object Login : Screens("login")
    object Register : Screens("register")
}

// Function to determine the start destination
fun determineStartDestination(isLoggedIn: Boolean,context: Context): String {
    var pass=utils.EncryptedPref.getData(context, noprofit.foss.KeysinEncryptedPref.PASSWORD)
    var hasPassword=false
    if (pass!=""&& pass!=null) hasPassword=true
    //val hasPassword= utils.EncryptedPref.getData(context, noprofit.foss.KeysinEncryptedPref.PASSWORD)!=""
    //android.util.Log.d("CHECK WHERE TO NAV","$pass")//debug use
    if (!hasPassword) return Screens.Register.route
    var testscreen=when {
        isLoggedIn ->Screens.Home.route
        else -> Screens.Login.route
    }
    //android.util.Log.d("HANDLE NAV BAR,auth,screen>","$isLoggedIn  $testscreen")//true but why go login screen?
    return when {
        isLoggedIn ->Screens.Home.route
        else -> Screens.Login.route
    }
}