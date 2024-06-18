package com.example.unievents.ui.screens.User
/*
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.unievents.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.math.*

@Composable
fun DirectionArrow(
    userLatitude: Double?,
    userLongitude: Double?,
    eventLatitude: Double,
    eventLongitude: Double
) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val azimuth = remember { mutableStateOf(0f) }
    val bearing = calculateBearing(userLatitude, userLongitude, eventLatitude, eventLongitude)

    LaunchedEffect(Unit) {
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        azimuth.value = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(sensorEventListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    val rotation = (bearing - azimuth.value).toFloat()

    Image(
        painter = painterResource(id = R.drawable.arrow),
        contentDescription = "Direction Arrow",
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer(rotationZ = rotation)
    )
}

fun calculateBearing(userLat: Double?, userLon: Double?, eventLat: Double, eventLon: Double): Float {
    if (userLat == null || userLon == null) return 0f
    val userLocation = Location("user").apply {
        latitude = userLat
        longitude = userLon
    }
    val eventLocation = Location("event").apply {
        latitude = eventLat
        longitude = eventLon
    }
    return userLocation.bearingTo(eventLocation)
}
*/