package com.example.unievents.data

import android.graphics.Bitmap

data class Ticket(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val qrCode: String = "",
    val status: String = "unused"
)
