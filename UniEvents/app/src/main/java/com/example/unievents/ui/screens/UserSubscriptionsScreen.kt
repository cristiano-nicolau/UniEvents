package com.example.unievents.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSubscriptionsScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val subscriptions = remember { mutableStateOf(listOf<Event>()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        eventRepository.getUserSubscriptions { eventList ->
            subscriptions.value = eventList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Subscriptions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(subscriptions.value) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { navController.navigate("eventDetails/${event.id}") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(event.title, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(event.location, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                navController.navigate("eventDetails/${event.id}")
                            }) {
                                Text("Show Ticket")
                            }
                            OutlinedButton(
                                onClick = {
                                    eventRepository.unsubscribeFromEvent(event.id) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Unsubscribed successfully!", Toast.LENGTH_SHORT).show()
                                            subscriptions.value = subscriptions.value.filter { it.id != event.id }
                                        } else {
                                            Toast.makeText(context, "Unsubscription failed!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Unsubscribe")
                            }
                        }
                    }
                }
            }
        }
    }
}
