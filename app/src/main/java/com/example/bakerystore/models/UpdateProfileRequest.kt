package com.example.bakerystore.models

data class UpdateProfileRequest(
    val fullName: String,
    val phone: String?
)