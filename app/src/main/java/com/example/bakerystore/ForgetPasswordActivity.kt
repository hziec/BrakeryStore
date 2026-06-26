package com.example.bakerystore

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerystore.models.ForgotPasswordCheckRequest
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.ResetPasswordRequest
import com.example.bakerystore.utils.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnContinue: Button
    private lateinit var layoutNewPassword: LinearLayout
    private lateinit var edtNewPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var tvBackToLogin: TextView

    private var checkedEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forget_password)

        edtEmail = findViewById(R.id.edtEmail)
        btnContinue = findViewById(R.id.btnContinue)
        layoutNewPassword = findViewById(R.id.layoutNewPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)

        layoutNewPassword.visibility = View.GONE

        btnContinue.setOnClickListener {
            handleCheckEmail()
        }

        btnResetPassword.setOnClickListener {
            handleResetPassword()
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun handleCheckEmail() {
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            edtEmail.error = "Vui lòng nhập email"
            edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Email không hợp lệ"
            edtEmail.requestFocus()
            return
        }

        btnContinue.isEnabled = false
        btnContinue.text = "Đang kiểm tra..."

        val request = ForgotPasswordCheckRequest(email)
        ApiClient.apiService.checkForgotPasswordEmail(request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    btnContinue.isEnabled = true
                    btnContinue.text = "Tiếp tục"

                    if (response.isSuccessful) {
                        checkedEmail = email
                        layoutNewPassword.visibility = View.VISIBLE
                        btnContinue.visibility = View.GONE
                        edtEmail.isEnabled = false
                        Toast.makeText(this@ForgetPasswordActivity, "Email hợp lệ, vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, "Lỗi: Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    btnContinue.isEnabled = true
                    btnContinue.text = "Tiếp tục"
                    Toast.makeText(this@ForgetPasswordActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun handleResetPassword() {
        val newPassword = edtNewPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (newPassword.isEmpty()) {
            edtNewPassword.error = "Vui lòng nhập mật khẩu mới"
            edtNewPassword.requestFocus()
            return
        }

        if (newPassword.length < 6) {
            edtNewPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            edtNewPassword.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
            edtConfirmPassword.requestFocus()
            return
        }

        if (newPassword != confirmPassword) {
            edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            edtConfirmPassword.requestFocus()
            return
        }

        btnResetPassword.isEnabled = false
        btnResetPassword.text = "Đang xử lý..."

        val request = ResetPasswordRequest(checkedEmail, newPassword, confirmPassword)
        ApiClient.apiService.resetPassword(request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Đổi mật khẩu"

                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgetPasswordActivity, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgetPasswordActivity, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    btnResetPassword.isEnabled = true
                    btnResetPassword.text = "Đổi mật khẩu"
                    Toast.makeText(this@ForgetPasswordActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
