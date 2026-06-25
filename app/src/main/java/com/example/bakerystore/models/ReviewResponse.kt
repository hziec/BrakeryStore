package com.example.bakerystore.models

data class ReviewResponse(
    val reviewId: Int,
    val productId: Int,
    val userId: Int,
    val fullName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String?
)