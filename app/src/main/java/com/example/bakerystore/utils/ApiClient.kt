package com.example.bakerystore.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Backend của bạn đang chạy: http://localhost:5207
    // Android Emulator gọi localhost máy tính bằng 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:5207/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}