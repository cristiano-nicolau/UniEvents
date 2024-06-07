package com.example.unievents.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UserSubscriptionsScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val subscriptions = remember { mutableStateOf(listOf<Event>()) }

    LaunchedEffect(Unit) {
        eventRepository.getUserSubscriptions { eventList ->
            subscriptions.value = eventList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Subscriptions") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) {
        LazyColumn {
            items(subscriptions.value) { event ->
                EventItem(event = event) {
                    navController.navigate("eventDetails/${event.id}")
                }
            }
        }
    }
}
