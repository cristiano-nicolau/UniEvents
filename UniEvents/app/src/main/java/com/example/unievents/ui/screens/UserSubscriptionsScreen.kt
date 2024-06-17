package com.example.unievents.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSubscriptionsScreen(navController: NavController) {
    val ticketRepository = remember { TicketRepository() }
    val subscribedEvents = remember { mutableStateOf<List<Event>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ticketRepository.getUserSubscriptions { events ->
            subscribedEvents.value = events
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(8.dp)
        ) {
            SearchBar()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Subscribed Events", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (subscribedEvents.value.isNotEmpty()) {
                LazyColumn(
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subscribedEvents.value) { event ->
                        EventItem(event = event, onClick = {
                            navController.navigate("eventDetails/${event.id}")
                        })
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("No subscribed events")
                }
            }
        }
    }
}

