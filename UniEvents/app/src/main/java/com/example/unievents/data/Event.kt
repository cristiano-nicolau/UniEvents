package com.example.unievents.data

import com.google.firebase.Timestamp

data class Event(
    var id: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    var organizer: String = "",
    val description: String = "",
    val capacity: Int = 0,
    val attendees: List<String> = emptyList(),
    val imageUrl: String = "",
    val attendeesCount: Int = 0,
    val latitude : Double = 0.0,
    val longitude : Double = 0.0
)