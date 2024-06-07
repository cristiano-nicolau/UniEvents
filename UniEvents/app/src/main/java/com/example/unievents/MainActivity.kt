package com.example.unievents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.unievents.ui.screens.AdminHomeScreen
import com.example.unievents.ui.screens.LoginScreen
import com.example.unievents.ui.screens.RegisterScreen
import com.example.unievents.ui.screens.UserHomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniEventsApp()
        }
    }
}

@Composable
fun UniEventsApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("adminHome") { AdminHomeScreen() }
        composable("userHome") { UserHomeScreen() }
    }
}
