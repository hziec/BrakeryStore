package com.example.bakerystore.models

import com.google.gson.annotations.SerializedName

data class CheckoutRequest(
    val userId: Int,
    val receiverName: String,
    val phone: String,
    val street: String,
    val city: String,
    val district: String,
    val ward: String,
    val paymentMethod: String,

    @SerializedName("cartItemIds")
    val cartItemIds: List<Int>
)