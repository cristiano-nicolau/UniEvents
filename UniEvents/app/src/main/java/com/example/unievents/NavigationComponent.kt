package com.example.unievents

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unievents.ui.screens.AdminHomeScreen
import com.example.unievents.ui.screens.LoginScreen
import com.example.unievents.ui.screens.RegisterScreen
import com.example.unievents.ui.screens.SplashScreen
import com.example.unievents.ui.screens.UserHomeScreen

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("userHome") { UserHomeScreen() }
        composable("adminHome") { AdminHomeScreen() }
    }
}
