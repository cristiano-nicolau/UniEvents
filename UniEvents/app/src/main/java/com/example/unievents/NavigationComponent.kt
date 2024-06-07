package com.example.unievents

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unievents.ui.screens.*

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("userHome") { UserHomeScreen(navController) }
        composable("adminHome") { AdminHomeScreen() }
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            if (eventId != null) {
                EventDetailsScreen(navController, eventId)
            }
        }
        composable("userSubscriptions") {
            UserSubscriptionsScreen(navController)
        }
    }
}
