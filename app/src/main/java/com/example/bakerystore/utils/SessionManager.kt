package com.example.bakerystore.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("BAKERY_SESSION", Context.MODE_PRIVATE)

    fun saveUser(
        userId: Int,
        fullName: String,
        email: String,
        phone: String?,
        role: String?
    ) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PHONE, phone ?: "")
            .putString(KEY_ROLE, role ?: "User")
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 0)
    }

    fun getFullName(): String {
        return prefs.getString(KEY_FULL_NAME, "") ?: ""
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun getPhone(): String {
        return prefs.getString(KEY_PHONE, "") ?: ""
    }

    fun getRole(): String {
        return prefs.getString(KEY_ROLE, "User") ?: "User"
    }

    fun updateFullName(fullName: String) {
        prefs.edit()
            .putString(KEY_FULL_NAME, fullName)
            .apply()
    }

    fun updatePhone(phone: String?) {
        prefs.edit()
            .putString(KEY_PHONE, phone ?: "")
            .apply()
    }

    fun logout() {
        prefs.edit()
            .clear()
            .apply()
    }

    companion object {
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_FULL_NAME = "FULL_NAME"
        private const val KEY_EMAIL = "EMAIL"
        private const val KEY_PHONE = "PHONE"
        private const val KEY_ROLE = "ROLE"
        private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    }
}