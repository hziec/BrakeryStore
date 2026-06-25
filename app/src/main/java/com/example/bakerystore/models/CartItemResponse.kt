package com.example.bakerystore.models

data class CartItemResponse(
    val cartItemId: Int,
    val userId: Int,
    val productId: Int,
    val productName: String,
    val description: String?,
    val imageUrl: String?,
    val price: Double,
    val quantity: Int,
    val total: Double
)