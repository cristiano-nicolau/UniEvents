package com.example.unievents.ui.screens.User

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unievents.R
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(navController: NavController, eventId: String) {
    val eventRepository = remember { EventRepository() }
    val ticketRepository = remember { TicketRepository() }
    val event = remember { mutableStateOf<Event?>(null) }
    val isSubscribed = remember { mutableStateOf(false) }
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
                title = { Text("My Ticket") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { contentPadding ->
        event.value?.let { eventDetails ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Event Logo",
                    modifier = Modifier
                        .size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${eventDetails.name} - ${eventDetails.organizer}",// centrado ao meio da pagina
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center
                )
                Text(
                    text = eventDetails.location,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = "People Icon")
                    Text(
                        text = "${eventDetails.attendeesCount} / ${eventDetails.capacity} People",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Map component
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    MapScreen()
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Direction",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Placeholder for the direction arrow
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray)
                ) {
                    // TODO: Implement Direction Arrow here
                    Text("Direction Arrow", modifier = Modifier.align(Alignment.Center))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "12 meters away",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        ticketRepository.getTicket(eventDetails.id) { ticket ->
                            if (ticket != null) {
                                qrCodeBitmap.value = ticketRepository.base64ToBitmap(ticket.qrCode)
                                showQrCodeDialog.value = true
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to retrieve ticket",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Show Ticket",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                if (showQrCodeDialog.value) {
                    AlertDialog(

                        onDismissRequest = { showQrCodeDialog.value = false },
                        title = { Text("Your Ticket QR Code",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),

                            ) },
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


                            Button(onClick = { showQrCodeDialog.value = false }, modifier = Modifier
                                .fillMaxWidth())
                            {
                                Text("Close")

                            }
                        }
                    )
                }
            }
        }
    }
}