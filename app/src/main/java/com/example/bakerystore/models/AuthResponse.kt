package com.example.bakerystore.models

data class AuthResponse(
    val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String?
)