package com.example.unievents

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unievents.ui.screens.AdminHomeScreen
import com.example.unievents.ui.screens.AllEventsScreen
import com.example.unievents.ui.screens.EventDetailsScreen
import com.example.unievents.ui.screens.LoginScreen
import com.example.unievents.ui.screens.RegisterScreen
import com.example.unievents.ui.screens.SplashScreen
import com.example.unievents.ui.screens.UserHomeScreen
import com.example.unievents.ui.screens.UserProfile
import com.example.unievents.ui.screens.UserSubscriptionsScreen
import com.example.unievents.ui.theme.UniEventsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniEventsTheme {
                UniEventsApp()
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
    }
}


