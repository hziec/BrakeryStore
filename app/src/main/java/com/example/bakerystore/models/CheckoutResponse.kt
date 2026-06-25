package com.example.bakerystore.models

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName("Message")
    val message: String,
    @SerializedName("OrderId")
    val orderId: Int,
    @SerializedName("TotalAmount")
    val totalAmount: Double,
    @SerializedName("OrderStatus")
    val orderStatus: String?,
    @SerializedName("PaymentMethod")
    val paymentMethod: String?,
    @SerializedName("PaymentStatus")
    val paymentStatus: String?
)
