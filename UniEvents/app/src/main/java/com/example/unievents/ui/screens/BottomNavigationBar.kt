package com.example.unievents.ui.screens

import android.graphics.Rect
import android.graphics.drawable.shapes.Shape
import android.health.connect.datatypes.units.Percentage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "userHome"),
        BottomNavItem("My Tickets", Icons.Default.DateRange, "userSubscriptions"),
        BottomNavItem("Profile", Icons.Default.Person, "userProfile"),
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
            .background(color = MaterialTheme.colorScheme.surface),
        tonalElevation = 2.dp)

        {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                   if (currentRoute == item.route) {
                       Icon(
                           imageVector = item.icon,
                           contentDescription = item.label,
                           tint = MaterialTheme.colorScheme.primary
                       )
                   } else {
                       Icon(
                           imageVector = item.icon,
                           contentDescription = item.label
                       )
                   }
                },
                label = {
                    if (currentRoute == item.route) {
                        Box(
                            modifier = Modifier
                                .height(12.dp)
                                .width(12.dp)
                                .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(percent = 50))
                        )
                    } else {
                        Box(
                            modifier = Modifier.background(Color.Transparent)
                        ) {
                            Text(
                                text = item.label,
                                maxLines = 1
                            )
                        }
                    }
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
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


