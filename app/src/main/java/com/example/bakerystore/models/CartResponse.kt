package com.example.bakerystore.models

data class CartResponse(
    val items: List<CartItemResponse>,
    val totalAmount: Double
)