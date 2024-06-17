package com.example.unievents.ui.screens

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                     //   navController.navigate("AdminEvent/${event.id}")
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
        // quero a parte abaixo e ao lado  outro icon
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search events...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
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
              //  navController.navigate("addEvent")
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(56.dp)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Add Event")
        }
    }
}


