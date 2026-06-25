package com.example.bakerystore.models

data class ProductResponse(
    val productId: Int,
    val productName: String,
    val description: String?,
    val price: Double,
    val imageUrl: String?,
    val stock: Int,
    val categoryId: Int?,
    val categoryName: String?,
    val createdAt: String?,
    val reviewCount: Int,
    val averageRating: Double
)