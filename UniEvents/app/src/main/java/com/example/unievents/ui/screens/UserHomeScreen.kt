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
fun UserHomeScreen(navController: NavController) {
    val eventRepository = remember { EventRepository() }
    val ticketRepository = remember { TicketRepository() }
    val events = remember { mutableStateOf(listOf<Event>()) }
    val monthEvents = remember { mutableStateOf(listOf<Event>()) }
    val currentMonth = LocalDate.now().month


    LaunchedEffect(Unit) {
        eventRepository.getEvents { eventList ->
            events.value = eventList
            monthEvents.value = eventList.filter {
                convertStringToDate(it.date).month == currentMonth
            }
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

            SectionTitle(title = "Upcoming Events", navController = navController)
            LazyRow {
                items(monthEvents.value) { event ->
                    EventCard(event = event) {
                        navController.navigate("eventDetails/${event.id}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(title = "Event Recommendations", navController = navController)
            LazyColumn {
                items(events.value) { event ->
                    EventItem(event = event) {
                        Log.d("UserHomeScreen", "Event clicked: ${event.name}")
                        navController.navigate("eventDetails/${event.id}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    TextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search events...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun SectionTitle(title: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        TextButton(onClick = {  navController.navigate("allEvents")}) {
            Text("Show All",fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

fun convertStringToDate(dateString: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale( "pt", "pt"))
    return LocalDate.parse(dateString, formatter)
}