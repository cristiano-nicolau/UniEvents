package com.example.unievents.ui.screens.User

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.graphics.Bitmap
import android.util.Log
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unievents.R
import com.example.unievents.data.Event
import com.example.unievents.data.EventRepository
import com.example.unievents.data.TicketRepository
import kotlin.math.*


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
    val userLatitude = remember { mutableStateOf<Double?>(null) }
    val userLongitude = remember { mutableStateOf<Double?>(null) }
    val distance = remember { mutableStateOf<Double?>(null) }
    val arrowRotation = remember { mutableStateOf(0f) }

    LaunchedEffect(eventId) {
        eventRepository.getEvents { events ->
            event.value = events.find { it.id == eventId }
            event.value?.let {
                Log.d("TicketScreen", "Event details: $it")
                calculateDistanceIfPossible(it, userLatitude.value, userLongitude.value, distance)
            }
        }
        ticketRepository.subscribedOnEventOrNot(eventId) { subscribed ->
            isSubscribed.value = subscribed
        }
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            hasLocationPermission.value = isGranted
        }
    )

    LaunchedEffect(hasLocationPermission.value) {
        if (hasLocationPermission.value) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    Log.d("TicketScreen", "User location: lat=${it.latitude}, lon=${it.longitude}")
                    userLatitude.value = it.latitude
                    userLongitude.value = it.longitude
                    event.value?.let { eventDetails ->
                        calculateDistanceIfPossible(eventDetails, userLatitude.value, userLongitude.value, distance)
                    }
                }
            }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(userLatitude.value, userLongitude.value) {
        event.value?.let { eventDetails ->
            calculateDistanceIfPossible(eventDetails, userLatitude.value, userLongitude.value, distance)
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
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Event Logo",
                    modifier = Modifier
                        .size(80.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                    MapScreen(
                        latitude = eventDetails.latitude,
                        longitude = eventDetails.longitude,
                        userLatitude = userLatitude,
                        userLongitude = userLongitude
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Direction",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                DirectionArrow(
                    userLatitude = userLatitude.value,
                    userLongitude = userLongitude.value,
                    eventLatitude = eventDetails.latitude,
                    eventLongitude = eventDetails.longitude,
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = distance.value?.let { formatDistance(it) } ?: "Calculating distance...",
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



fun calculateDistanceIfPossible(event: Event, userLat: Double?, userLon: Double?, distance: MutableState<Double?>) {
    if (userLat != null && userLon != null) {
        distance.value = calculateDistance(
            userLat, userLon,
            event.latitude, event.longitude
        )
        Log.d("TicketScreen", "Calculated distance: ${distance.value} meters")
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    print("lat1: $lat1, lon1: $lon1, lat2: $lat2, lon2: $lon2")
    val earthRadius = 6371.0 // kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c * 1000 // convert to meters
}

fun formatDistance(distance: Double): String {
    return if (distance < 1000) {
        "${distance.toInt()} meters away"
    } else {
        String.format("%.2f km away", distance / 1000)
    }
}

