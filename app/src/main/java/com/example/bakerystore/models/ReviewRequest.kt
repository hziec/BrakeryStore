package com.example.bakerystore.models

data class ReviewRequest(
    val userId: Int,
    val productId: Int,
    val rating: Int,
    val comment: String?
)