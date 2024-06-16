package com.example.unievents.data

data class Ticket(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val qrCode: String = "",
    val status: String = "unused"
)
