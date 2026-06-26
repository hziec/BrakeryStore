package com.example.bakerystore.models

import com.google.gson.annotations.SerializedName

data class ForgotPasswordCheckRequest(
    @SerializedName("Email")
    val email: String
)
