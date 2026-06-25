package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerystore.models.AuthResponse
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.UpdatePasswordRequest
import com.example.bakerystore.models.UpdateProfileRequest
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtOldPassword: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnHistory: Button
    private lateinit var btnLogout: Button
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_profile)

        bindViews()
        setupLockedFields()
        setupEvents()
        loadProfile()
    }

    private fun bindViews() {
        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtOldPassword = findViewById(R.id.edtOldPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnHistory = findViewById(R.id.btnHistory)
        btnLogout = findViewById(R.id.btnLogout)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupLockedFields() {
        edtEmail.isEnabled = false
        edtPhone.isEnabled = false
    }

    private fun setupEvents() {
        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        btnChangePassword.setOnClickListener {
            changePassword()
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sessionManager.logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProfile() {
        ApiClient.apiService.getProfile(sessionManager.getUserId())
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!

                        edtFullName.setText(user.fullName)
                        edtEmail.setText(user.email)
                        edtPhone.setText(user.phone ?: "")

                        sessionManager.saveUser(
                            user.userId,
                            user.fullName,
                            user.email,
                            user.phone,
                            user.role
                        )
                    } else {
                        loadProfileFromSession()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    loadProfileFromSession()
                }
            })
    }

    private fun loadProfileFromSession() {
        edtFullName.setText(sessionManager.getFullName())
        edtEmail.setText(sessionManager.getEmail())
        edtPhone.setText(sessionManager.getPhone())
    }

    private fun updateProfile() {
        val fullName = edtFullName.text.toString().trim()

        if (fullName.isEmpty()) {
            edtFullName.error = "Tên không được để trống"
            edtFullName.requestFocus()
            return
        }

        btnUpdateProfile.isEnabled = false
        btnUpdateProfile.text = "Đang lưu..."

        val request = UpdateProfileRequest(
            fullName = fullName,
            phone = sessionManager.getPhone()
        )

        ApiClient.apiService.updateProfile(sessionManager.getUserId(), request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    btnUpdateProfile.isEnabled = true
                    btnUpdateProfile.text = "Lưu tên"

                    if (response.isSuccessful) {
                        sessionManager.updateFullName(fullName)

                        Toast.makeText(
                            this@ProfileActivity,
                            "Cập nhật tên thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Không cập nhật được tên",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    btnUpdateProfile.isEnabled = true
                    btnUpdateProfile.text = "Lưu tên"

                    Toast.makeText(
                        this@ProfileActivity,
                        "Lỗi cập nhật: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun changePassword() {
        val oldPassword = edtOldPassword.text.toString().trim()
        val newPassword = edtNewPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (oldPassword.isEmpty()) {
            edtOldPassword.error = "Vui lòng nhập mật khẩu cũ"
            edtOldPassword.requestFocus()
            return
        }

        if (newPassword.length < 6) {
            edtNewPassword.error = "Mật khẩu mới ít nhất 6 ký tự"
            edtNewPassword.requestFocus()
            return
        }

        if (newPassword != confirmPassword) {
            edtConfirmPassword.error = "Mật khẩu nhập lại không khớp"
            edtConfirmPassword.requestFocus()
            return
        }

        btnChangePassword.isEnabled = false
        btnChangePassword.text = "Đang đổi..."

        val request = UpdatePasswordRequest(
            userId = sessionManager.getUserId(),
            oldPassword = oldPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )

        ApiClient.apiService.changePassword(request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Đổi mật khẩu"

                    if (response.isSuccessful) {
                        edtOldPassword.text.clear()
                        edtNewPassword.text.clear()
                        edtConfirmPassword.text.clear()

                        Toast.makeText(
                            this@ProfileActivity,
                            "Đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ProfileActivity,
                            "Mật khẩu cũ không đúng hoặc đổi thất bại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "Đổi mật khẩu"

                    Toast.makeText(
                        this@ProfileActivity,
                        "Lỗi đổi mật khẩu: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}