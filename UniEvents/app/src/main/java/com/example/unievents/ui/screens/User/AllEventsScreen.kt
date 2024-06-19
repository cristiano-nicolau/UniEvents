package com.example.unievents.ui.screens.User


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.ui.screens.EventItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllEventsScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val events = remember { mutableStateOf(listOf<Event>()) }

    LaunchedEffect(Unit) {
        eventRepository.getEvents { eventList ->
            events.value = eventList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Events") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .padding(8.dp)
        ) {
            items(events.value) { event ->
                EventItem(event = event) {
                    navController.navigate("eventDetails/${event.id}")
                }
            }
        }
    }
}
