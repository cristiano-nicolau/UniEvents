package com.example.unievents

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.unievents.ui.screens.admin.AdminHomeScreen
import com.example.unievents.ui.screens.User.AllEventsScreen
import com.example.unievents.ui.screens.admin.CreateEventScreen
import com.example.unievents.ui.screens.User.EventDetailsScreen
import com.example.unievents.ui.screens.LoginScreen
import com.example.unievents.ui.screens.RegisterScreen
import com.example.unievents.ui.screens.SplashScreen
import com.example.unievents.ui.screens.User.UserHomeScreen
import com.example.unievents.ui.screens.User.UserProfile
import com.example.unievents.ui.screens.User.UserSubscriptionsScreen
import com.example.unievents.ui.theme.UniEventsTheme
import com.example.unievents.ui.screens.User.MapScreen
import com.example.unievents.ui.screens.User.TicketScreen
import com.example.unievents.ui.screens.admin.AdminEventDetails
class MainActivity : ComponentActivity() {
    private val notificationPermissionRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permissão de notificação se necessário
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            }
        }

        setContent {
            UniEventsTheme {
                UniEventsApp()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == notificationPermissionRequestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permissão concedida
            } else {
                // Permissão negada
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
fun UniEventsApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("adminHome") { AdminHomeScreen(navController) }
        composable("userProfile") { UserProfile(navController) }
        composable("userHome") { UserHomeScreen(navController) }
        composable("allEvents") {
            AllEventsScreen(navController)
        }
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EventDetailsScreen(navController, eventId)
            } else {
                Log.e("UniEventsApp", "Event ID is null")

            }
        }
        composable("userSubscriptions") {
            UserSubscriptionsScreen(navController)
        }
        composable("myTicket/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                TicketScreen(navController, eventId)
            } else {
                Log.e("UniEventsApp", "Event ID is null")

            }
        }
        composable("admin/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                AdminEventDetails(navController, eventId)
            } else {
                Log.e("UniEventsApp", "Event ID is null")

            }
        }
        composable("createEvent") {
            CreateEventScreen(navController)
        }

    }
}


