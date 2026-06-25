package com.example.bakerystore.models

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String?
)