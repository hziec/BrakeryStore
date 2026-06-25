package com.example.bakerystore.models

data class OrderDetailItemResponse(
    val productId: Int,
    val productName: String,
    val imageUrl: String?,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double
)