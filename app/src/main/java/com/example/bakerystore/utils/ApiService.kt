package com.example.bakerystore.utils

import com.example.bakerystore.models.AddToCartRequest
import com.example.bakerystore.models.AuthResponse
import com.example.bakerystore.models.CartResponse
import com.example.bakerystore.models.CategoryResponse
import com.example.bakerystore.models.CheckoutRequest
import com.example.bakerystore.models.CheckoutResponse
import com.example.bakerystore.models.LoginRequest
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.OrderDetailResponse
import com.example.bakerystore.models.OrderResponse
import com.example.bakerystore.models.ProductResponse
import com.example.bakerystore.models.RegisterRequest
import com.example.bakerystore.models.ReviewRequest
import com.example.bakerystore.models.ReviewResponse
import com.example.bakerystore.models.UpdateCartItemRequest
import com.example.bakerystore.models.UpdatePasswordRequest
import com.example.bakerystore.models.UpdateProfileRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // =========================
    // AUTH
    // =========================
    @POST("auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<AuthResponse>

    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    // =========================
    // CATEGORIES
    // =========================
    @GET("categories")
    fun getCategories(): Call<List<CategoryResponse>>

    @GET("categories/{id}")
    fun getCategoryById(
        @Path("id") categoryId: Int
    ): Call<CategoryResponse>

    // =========================
    // PRODUCTS
    // =========================
    @GET("products")
    fun getProducts(
        @Query("categoryId") categoryId: Int? = null,
        @Query("keyword") keyword: String? = null
    ): Call<List<ProductResponse>>

    @GET("products/{id}")
    fun getProductById(
        @Path("id") productId: Int
    ): Call<ProductResponse>

    // =========================
    // CART
    // =========================
    @GET("cart/user/{userId}")
    fun getCartByUser(
        @Path("userId") userId: Int
    ): Call<CartResponse>

    @POST("cart/add")
    fun addToCart(
        @Body request: AddToCartRequest
    ): Call<MessageResponse>

    @PUT("cart/update/{cartItemId}")
    fun updateCartItem(
        @Path("cartItemId") cartItemId: Int,
        @Body request: UpdateCartItemRequest
    ): Call<MessageResponse>

    @DELETE("cart/remove/{cartItemId}")
    fun removeCartItem(
        @Path("cartItemId") cartItemId: Int
    ): Call<MessageResponse>

    @DELETE("cart/clear/{userId}")
    fun clearCart(
        @Path("userId") userId: Int
    ): Call<MessageResponse>

    // =========================
    // ORDERS
    // =========================
    @POST("orders/checkout")
    fun checkout(
        @Body request: CheckoutRequest
    ): Call<CheckoutResponse>

    @GET("orders/user/{userId}")
    fun getOrdersByUser(
        @Path("userId") userId: Int
    ): Call<List<OrderResponse>>

    @GET("orders/{orderId}")
    fun getOrderDetail(
        @Path("orderId") orderId: Int
    ): Call<OrderDetailResponse>

    // =========================
    // USERS / PROFILE
    // =========================
    @GET("users/{userId}")
    fun getProfile(
        @Path("userId") userId: Int
    ): Call<AuthResponse>

    @PUT("users/{userId}")
    fun updateProfile(
        @Path("userId") userId: Int,
        @Body request: UpdateProfileRequest
    ): Call<MessageResponse>

    @PUT("users/change-password")
    fun changePassword(
        @Body request: UpdatePasswordRequest
    ): Call<MessageResponse>

    // =========================
    // REVIEWS
    // =========================
    @GET("reviews/product/{productId}")
    fun getReviewsByProduct(
        @Path("productId") productId: Int
    ): Call<List<ReviewResponse>>

    @POST("reviews")
    fun addReview(
        @Body request: ReviewRequest
    ): Call<MessageResponse>
}