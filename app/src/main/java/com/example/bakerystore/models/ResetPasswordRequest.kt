package com.example.bakerystore.models

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("Email")
    val email: String,
    @SerializedName("NewPassword")
    val newPassword: String,
    @SerializedName("ConfirmPassword")
    val confirmPassword: String
)
