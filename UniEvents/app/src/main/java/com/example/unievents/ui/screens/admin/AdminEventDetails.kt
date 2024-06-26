package com.example.unievents.ui.screens.admin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.example.unievents.QRCodeScannerActivity
import com.example.unievents.R
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.Ticket
import com.example.unievents.data.TicketRepository
import com.example.unievents.ui.screens.EventItem
import com.example.unievents.ui.screens.User.convertStringToDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventDetails(navController: NavController ,eventId: String) {
    val eventRepository = remember { EventRepository() }
    val ticketRepository = remember { TicketRepository() }
    val event = remember { mutableStateOf<Event?>(null) }
    val attendees = remember { mutableStateOf<List<Ticket>>(emptyList()) }

    LaunchedEffect(eventId) {
        eventRepository.getEvent(eventId) { eventValue ->
            event.value = eventValue
        }
        ticketRepository.getTicketsForEvent(eventId) { ticketList ->
            attendees.value = ticketList
        }
    }

    val context = LocalContext.current
    val qrCodeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val qrCode = result.data?.getStringExtra("QR_CODE")
            qrCode?.let {

                Toast.makeText(context, "QR Code Scanned: $qrCode", Toast.LENGTH_LONG).show()
                ticketRepository.validateTicket(qrCode) { isValid ->
                    if (isValid) {
                        Toast.makeText(context, "Ticket validado com sucesso!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Falha na validação do ticket.", Toast.LENGTH_LONG).show()
                    }
                }
                navController.popBackStack()
            }
        } else {
            Toast.makeText(context, "Falha ao ler o QR code.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        event.value?.let { event ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Event Image",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${attendees.value.count { it.status == "active" }}/${event.capacity} active",
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    if (attendees.value.isNotEmpty()) {
                        items(attendees.value) { ticket ->
                            AttendeeItem(ticket)
                        }
                    } else {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("No attendees")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, QRCodeScannerActivity::class.java)
                        qrCodeLauncher.launch(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)

                ) {
                    Text("READ QR CODE")
                }
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}
@Composable
fun AttendeeItem(ticket: Ticket) {
    val statusColor = when (ticket.status) {
        "unused" -> Color.Gray
        "using" -> Color.Green
        "used" -> Color.Red
        else -> Color.Gray
    }
    val text = when (ticket.status) {
        "unused" -> "Unused"
        "using" -> "Entered"
        "used" -> "Exited"
        else -> "Unused"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ticket.email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
        Text(
            text = text.uppercase(),
            modifier = Modifier
                .background(statusColor, shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 4.dp, horizontal = 16.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}