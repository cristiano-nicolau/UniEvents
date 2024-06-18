package com.example.unievents

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable("map") { MapScreen() }
        composable("createEvent") {
            CreateEventScreen(navController)
        }

    }
}


