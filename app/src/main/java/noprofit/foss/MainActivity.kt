package noprofit.foss

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
//pure xml way
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
*/

//compose
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.ComposeView
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import noprofit.foss.ui.theme.NavigationBarMediumTheme
//notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController

//hilt
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import noprofit.foss.NOSQL.NoSQLHelper

@AndroidEntryPoint
public class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var noSQLHelperobj: NoSQLHelper//WARN ALL OTHER NOSQLHELPER OBJECT MUST USE SAME NAME AS THIS.AT IT IS CREATED SINGLETON.

    @Inject
    lateinit var loginscnrbackendSingleton: LoginbackendSingleton

    private val authvm: Loginscnrbackend
        get() = loginscnrbackendSingleton.getInstance()


    //appwide navigation
    lateinit var navController: androidx.navigation.NavHostController

    //src,https://kotlinlang.org/docs/null-safety.html#nullable-types-and-non-nullable-types
    public var appwidenotifymanager:NotificationManager? =null

    //notification
    public fun testshowNotification() {
        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hello world")
            .setContentText("This is a description")
            .build()
        this.appwidenotifymanager?.notify(1, notification)
    }

    //notification
    public fun addnotificationAPI(title:String,descr:String,id:Int) {
        val notification = NotificationCompat.Builder(applicationContext, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(descr)
            .build()
        this.appwidenotifymanager?.notify(id, notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        appwidenotifymanager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Testapp",
                "Budget update",
                NotificationManager.IMPORTANCE_HIGH
            )
            //appwidenotifymanager.createNotificationChannel(channel)
        }

        //setup compose.xml way
        val tstcompview: ComposeView = findViewById(R.id.tstcompose_view)
        tstcompview.setContent {
            navController = rememberNavController()


            // Step 1: UI & Screens Setup
            /**way3 try add extra stuff before calling the nav bar.
             * Here is the step 1 of code for bottom navigation bar. After the xml composeview setup and compose setup in code.
             * which is just overwrite the whole surface and give it ot navbar controller.
             * Then next step is setup each 'place', refer Bottomnavdts.kt in this proj.
             * */
            NavigationBarMediumTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val authViewModel: Loginscnrbackend = hiltViewModel()
                    BottomNavigationBar(noSQLHelperobj,navController,authvm)

                }
            }
        }
    }

    /***
     * this way assume is compose and only have 1 activity else will prrsszaza, because traditional android app has multiple activity
    //encrypt on unactive&logout,ai&src,https://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
    */
    private var isVisible: Boolean = false

    override fun onResume() {
        super.onResume()

        /**
        //v2,to prevent annonying relogin
        // Check if the intent should be ignored or handled.Here triggers the login check.
        */
        intent?.let {
            val action = it.action
            val data = it.data

            var iscoreappintent=false
            if (action == Intent.ACTION_CREATE_DOCUMENT||(action== Intent.ACTION_OPEN_DOCUMENT)||(action== Intent.ACTION_GET_CONTENT)||(action == Intent.ACTION_BATTERY_CHANGED)){iscoreappintent=true}
            // logic to ignore or handle the intent
            if (!iscoreappintent) {//allow and,Handle the intent here
                //android.util.Log.d("INTENT in main act","$action")
                updateVisibility(true)//here triggered
            }
        }

    }

    override fun onPause() {
        super.onPause()
        intent?.let {
            val action = it.action
            val data = it.data

            var iscoreappintent=false
            //prevent logout by core feature.
            if (action == Intent.ACTION_CREATE_DOCUMENT||(action== Intent.ACTION_OPEN_DOCUMENT)||(action== Intent.ACTION_MAIN)||(action== Intent.ACTION_GET_CONTENT)||(action == Intent.ACTION_BATTERY_CHANGED)){iscoreappintent=true}
            // logic to ignore or handle the intent
            if (!iscoreappintent) {//allow and,Handle the intent here
                android.util.Log.d("INTENT in main act","$action")
                // Handle logic when the activity goes to the background or is switched
                updateVisibility(false)
            }
        }

    }

    private fun updateVisibility(isVisible: Boolean) {
        this.isVisible = isVisible
        if (!isVisible) {
            authvm.saveencryptnosql(this)
            authvm.ForceLOGOUT() // Your composable code using the ViewModel
        }else{
            //check if there are encrypted data if yes then sent them to login screen.
            //Ensure navController is initialized before using it
            //should have let the nav logic control here,,,, duplicate logic checking.
            if(!authvm.isLoggedIn.value){
                if (::navController.isInitialized) {
                    android.util.Log.d("In main act,force go login>","$")
                    navController.navigate(Screens.Login.route) {
                        // Clear the back stack to prevent navigating back to the previous screen
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }

}