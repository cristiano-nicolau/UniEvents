package com.example.unievents.data

data class Event(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val destination: String = "",
    val location: String = "",
    val organizer: User = User(),
    val capacity: Int = 0,
    val attendees: Int = 0
)