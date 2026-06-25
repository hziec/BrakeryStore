package com.example.bakerystore.models

data class AddToCartRequest(
    val userId: Int,
    val productId: Int,
    val quantity: Int
)