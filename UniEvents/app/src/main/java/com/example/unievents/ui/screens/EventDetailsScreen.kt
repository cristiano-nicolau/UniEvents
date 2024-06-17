package com.example.unievents.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import com.example.unievents.R
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(navController: NavController, eventId: String) {
    val eventRepository = remember { EventRepository() }
    val ticketRepository = remember { TicketRepository() }
    val event = remember { mutableStateOf<Event?>(null) }
    val isSubscribed = remember { mutableStateOf(false) }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showQrCodeDialog = remember { mutableStateOf(false) }
    val qrCodeBitmap = remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(eventId) {
        eventRepository.getEvents { events ->
            event.value = events.find { it.id == eventId }
        }
        ticketRepository.subscribedOnEventOrNot(eventId) { subscribed ->
            isSubscribed.value = subscribed
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        event.value?.let { eventDetails ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = eventDetails.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = eventDetails.date,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                    Text(
                        text = " | ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                    Text(
                        text = eventDetails.time,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = eventDetails.location,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Organizer")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = eventDetails.organizer,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "About Event",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = eventDetails.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Capacity")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Capacity: ${eventDetails.capacity} Users",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Going")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+${eventDetails.attendeesCount} Going",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (isSubscribed.value) {
                    Button(
                        onClick = {
                            // Retrieve the ticket to get the QR code Base64 string
                            ticketRepository.getTicket(eventDetails.id) { ticket ->
                                if (ticket != null) {
                                    qrCodeBitmap.value = ticketRepository.base64ToBitmap(ticket.qrCode)
                                    showQrCodeDialog.value = true
                                } else {
                                    Toast.makeText(context, "Failed to retrieve ticket", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .background( MaterialTheme.colorScheme.secondary,RoundedCornerShape(8.dp)),
                          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)

                    ) {
                        Text("SHOW TICKET", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        showConfirmationDialog.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .background( if (isSubscribed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSubscribed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                ) {
                    Text(if (isSubscribed.value) "UNSUBSCRIBE" else "SUBSCRIBE", style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                }

                if (showQrCodeDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showQrCodeDialog.value = false },
                        title = { Text("Your Ticket QR Code") },
                        text = {
                            qrCodeBitmap.value?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(200.dp)
                                )
                            } ?: Text("QR Code could not be generated")
                        },
                        confirmButton = {
                            Button(onClick = { showQrCodeDialog.value = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                if (showConfirmationDialog.value) {
                    AlertDialog(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(12.dp)),
                        onDismissRequest = { showConfirmationDialog.value = false },
                        title = {
                            Text("CONFIRM ${if (isSubscribed.value) "UNSUBSCRIPTION" else "SUBSCRIPTION"}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        text = {
                            Text("Are you sure you want to ${if (isSubscribed.value) "unsubscribe from" else "subscribe to"} this event?",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )
                        },
                        confirmButton = {
                            TextButton(
                                modifier = Modifier.weight(5f).background( if (isSubscribed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor  = if (isSubscribed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary),
                                onClick = {
                                    if (isSubscribed.value) {
                                        ticketRepository.unsubscribeFromEvent(eventDetails.id) { success ->
                                            if (success) {
                                                Toast.makeText(context, "Unsubscribed successfully!", Toast.LENGTH_SHORT).show()
                                                isSubscribed.value = false
                                            } else {
                                                Toast.makeText(context, "Unsubscription failed!", Toast.LENGTH_SHORT).show()
                                            }
                                            showConfirmationDialog.value = false
                                        }
                                    } else {
                                        ticketRepository.generateTicket(eventDetails.id) { ticket ->
                                            if (ticket) {
                                                Toast.makeText(context, "Subscribed successfully!", Toast.LENGTH_SHORT).show()
                                                isSubscribed.value = true
                                            } else {
                                                Toast.makeText(context, "Subscription failed!", Toast.LENGTH_SHORT).show()
                                            }
                                            showConfirmationDialog.value = false
                                        }
                                    }
                                }
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                modifier = Modifier.weight(5f).background( Color.LightGray, shape = RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.buttonColors(contentColor  = if (isSubscribed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary, containerColor = Color.LightGray),
                                onClick = { showConfirmationDialog.value = false }
                            ) {

                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}
