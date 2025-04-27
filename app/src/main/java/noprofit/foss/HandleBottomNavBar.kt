package noprofit.foss

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import noprofit.foss.screens.HomeScreen
import noprofit.foss.screens.ProfileScreen

//for auth
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noprofit.foss.NOSQL.NoSQLHelper

// Step 1: UI & Screens Setup

/**
 * //removed.THe extra activity arguement is for passing around the notification object and app infos
 * */
@Composable
fun BottomNavigationBar(noSQLHelper : NoSQLHelper, navController: androidx.navigation.NavHostController, authvm : Loginscnrbackend) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    //singleton time.

    val isLoggedIn= authvm.isLoggedIn.collectAsState()
    val contextforauth= LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Debug log for checking the ViewModel instance and isLoggedIn state
            //android.util.Log.d("home", "authvm instance: $authvm")//debug use
            //android.util.Log.d("Bottom Navigation Bar check login","$isLoggedIn")
            if (isLoggedIn.value){//patch prevent user bypass go to other page bf login.
                NavigationBar (modifier = Modifier.height(88.dp)){
                    BottomNavItems().forEachIndexed { _, navigationItem ->
                        NavigationBarItem(
                            selected = navigationItem.route == currentDestination?.route,
                            label = {
                                Text(navigationItem.label)
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label
                                )
                            },
                            onClick = {
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ){paddingValues ->
        NavHost(
            navController = navController,
            startDestination = determineStartDestination(isLoggedIn.value,contextforauth) ,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            //added login screen
            composable("login") {
                noprofit.foss.screens.LoginScreen(authvm,
                    onLoginSuccess = {
                        navController.navigate(Screens.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screens.Register.route) {
                noprofit.foss.screens.RegisterScreen(
                    onSuccess = {
                        navController.navigate(Screens.Home.route) { popUpTo(Screens.Register.route) { inclusive = true } }
                    }
                )
            }
            composable(Screens.Home.route) {
              HomeScreen(navController,noSQLHelper) //call our composable screens here
            }
            composable(Screens.Statistic.route) {
                noprofit.foss.screens.StatisticUI(navController,noSQLHelper)
            }
            composable(Screens.Profile.route) {
                ProfileScreen(navController)
            }
        }
    }
}

