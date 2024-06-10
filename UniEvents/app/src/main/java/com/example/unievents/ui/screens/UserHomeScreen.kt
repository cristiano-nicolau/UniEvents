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
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val events = remember { mutableStateOf(listOf<Event>()) }

    LaunchedEffect(Unit) {
        eventRepository.getEvents { eventList ->
            events.value = eventList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Button(onClick = { navController.navigate("myTickets") }) {
                Text("My Subscriptions")
            }
            LazyColumn {
                items(events.value) { event ->
                    EventItem(event = event) {
                        navController.navigate("eventDetails/${event.id}")
                    }
                }
            }
        }
    }
}
