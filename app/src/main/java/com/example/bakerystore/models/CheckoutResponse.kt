package com.example.bakerystore.models

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName(value = "message", alternate = ["Message"])
    val message: String? = null,

    @SerializedName(value = "orderId", alternate = ["OrderId", "id", "Id"])
    val orderId: Int = 0,

    @SerializedName(value = "totalAmount", alternate = ["TotalAmount"])
    val totalAmount: Double = 0.0,

    @SerializedName(value = "orderStatus", alternate = ["OrderStatus"])
    val orderStatus: String? = null,

    @SerializedName(value = "paymentMethod", alternate = ["PaymentMethod"])
    val paymentMethod: String? = null,

    @SerializedName(value = "paymentStatus", alternate = ["PaymentStatus"])
    val paymentStatus: String? = null
)