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
fun EventDetailsScreen(navController: NavController, eventId: String) {
    val eventRepository = remember { EventRepository() }
    val event = remember { mutableStateOf<Event?>(null) }
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        eventRepository.getEvents { events ->
            event.value = events.find { it.id == eventId }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Event Details") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) {
        event.value?.let { eventDetails ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(eventDetails.title, style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(8.dp))
                Text(eventDetails.description, style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Date: ${eventDetails.date}", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Time: ${eventDetails.time}", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Location: ${eventDetails.location}", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Organizer: ${eventDetails.organizer.name}", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    eventRepository.subscribeToEvent(eventId) { success ->
                        if (success) {
                            Toast.makeText(context, "Subscribed successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Subscription failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Subscribe")
                }
            }
        }
    }
}
