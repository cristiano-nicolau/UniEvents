package com.example.unievents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.unievents.ui.theme.UniEventsTheme
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.unievents.ui.theme.UniEventsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniEventsTheme {
                val navController = rememberNavController()
                var isAuthenticated by remember { mutableStateOf(false) }
                var isAdmin by remember { mutableStateOf(false) }

                // Mock authentication state, replace this with actual authentication logic
                LaunchedEffect(Unit) {
                    isAuthenticated = true // Replace with actual auth logic
                    isAdmin = false // Replace with actual role check
                }

                if (isAuthenticated) {
                    if (isAdmin) {
                        navController.navigate("adminHome")
                    } else {
                        navController.navigate("userHome")
                    }
                } else {
                    navController.navigate("login")
                }

                NavigationComponent(navController)
            }
        }
    }
}
