package com.example.bakerystore.models

data class OrderResponse(
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String?,
    val deliveryDate: String?,
    val createdAt: String?,
    val paymentMethod: String?,
    val paymentStatus: String?,
    val shippingAddress: String?
)