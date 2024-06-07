package com.example.unievents.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photo: String = "",
    val role: String = ""  // "user" or "admin"
)
