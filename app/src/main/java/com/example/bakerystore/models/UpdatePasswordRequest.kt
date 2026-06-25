package com.example.bakerystore.models

data class UpdatePasswordRequest(
    val userId: Int,
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)