package com.example.unievents.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photo: String = "",
    val role: String = "", // "user" or "admin"
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
