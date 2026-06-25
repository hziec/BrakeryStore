package com.example.bakerystore.models

data class OrderDetailResponse(
    val orderId: Int,
    val userId: Int,
    val totalAmount: Double,
    val status: String?,
    val deliveryDate: String?,
    val createdAt: String?,
    val paymentMethod: String?,
    val paymentStatus: String?,
    val shippingAddress: String?,
    val items: List<OrderDetailItemResponse>
)