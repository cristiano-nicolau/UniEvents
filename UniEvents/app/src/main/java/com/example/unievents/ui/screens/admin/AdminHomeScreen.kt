package com.example.unievents.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import com.example.unievents.data.AuthRepository
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository
import com.example.unievents.ui.screens.EventItem
import com.example.unievents.ui.screens.User.convertStringToDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val ticketRepository = remember { TicketRepository() }
    val events = remember { mutableStateOf(listOf<Event>()) }
    val monthEvents = remember { mutableStateOf(listOf<Event>()) }
    val currentMonth = LocalDate.now().month


    LaunchedEffect(Unit) {
        eventRepository.getEvents { eventList ->
            events.value = eventList
            val filteredEvents = eventList.filter { event ->
                val eventDate = convertStringToDate(event.date)
                eventDate >= LocalDate.now()
            }.sortedBy { event ->
                convertStringToDate(event.date)
            }
            monthEvents.value = filteredEvents
        }
    }

    Scaffold(
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(8.dp)
        ) {
            SearchBarAdmin(navController)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Events",style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold
            )
            LazyColumn {
                items(events.value) { event ->
                    EventItem(event = event) {
                        Log.d("UserHomeScreen", "Event clicked: ${event.name}")
                        navController.navigate("admin/${event.id}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarAdmin(navController: NavController) {
    Row {
        IconButton(
            onClick = {
                AuthRepository().logoutUser {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
        }
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search events...") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(
            onClick = {
              navController.navigate("createEvent")
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add Event")
        }
    }
}


