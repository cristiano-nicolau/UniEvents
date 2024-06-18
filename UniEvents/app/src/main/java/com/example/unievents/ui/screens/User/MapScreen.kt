package com.example.unievents.ui.screens.User

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.example.unievents.data.AuthRepository
import com.example.unievents.data.User
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationRequest


@Composable
fun MapScreen(latitude : Double, longitude : Double, userLatitude: MutableState<Double?>, userLongitude: MutableState<Double?>) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 10f)
    }

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

    LaunchedEffect(Unit) {
        if (!hasLocationPermission.value) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val user = remember { mutableStateOf<User?>(null) }
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            authRepository.getUser { result ->
                user.value = result
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasLocationPermission.value) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                Marker(
                    state = rememberMarkerState(position = LatLng(latitude, longitude)),
                    title = "Event Location"
                )
            }
            user.value?.let { currentUser ->
                TrackLocationAndUpdateFirestore(context, currentUser.email, userLatitude, userLongitude)
            }
        } else {
            Text(
                "Location permission is required to show the map.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun TrackLocationAndUpdateFirestore(context: android.content.Context, userEmail: String, userLatitude: MutableState<Double?>, userLongitude: MutableState<Double?>) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val db = Firebase.firestore

    LaunchedEffect(Unit) {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLatitude.value = location.latitude
                    userLongitude.value = location.longitude
                    db.collection("users").whereEqualTo("email", userEmail).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                db.collection("users").document(document.id)
                                    .update(mapOf(
                                        "latitude" to latLng.latitude,
                                        "longitude" to latLng.longitude
                                    ))
                                    .addOnSuccessListener {
                                        // Handle success
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error updating document: $e", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error getting documents: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
}
