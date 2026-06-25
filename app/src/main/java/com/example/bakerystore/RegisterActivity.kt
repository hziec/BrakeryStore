package com.example.bakerystore

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerystore.models.AuthResponse
import com.example.bakerystore.models.RegisterRequest
import com.example.bakerystore.utils.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var tvGoLogin: TextView
    private lateinit var btnBack: android.widget.ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoLogin = findViewById(R.id.tvGoLogin)
        btnBack = findViewById(R.id.btnBack)

        btnRegister.setOnClickListener {
            handleRegister()
        }

        tvGoLogin.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun handleRegister() {
        val fullName = edtFullName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            edtFullName.error = getString(R.string.error_empty_fullname)
            edtFullName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            edtEmail.error = getString(R.string.error_empty_email)
            edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = getString(R.string.error_invalid_email)
            edtEmail.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            edtPhone.error = getString(R.string.error_empty_phone)
            edtPhone.requestFocus()
            return
        }

        if (password.length < 6) {
            edtPassword.error = getString(R.string.error_password_short)
            edtPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            edtConfirmPassword.error = getString(R.string.error_password_mismatch)
            edtConfirmPassword.requestFocus()
            return
        }

        if (!cbTerms.isChecked) {
            Toast.makeText(this, R.string.error_terms_not_accepted, Toast.LENGTH_SHORT).show()
            return
        }

        btnRegister.isEnabled = false
        btnRegister.text = getString(R.string.register_loading)

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            phone = phone
        )

        ApiClient.apiService.register(request)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.btn_register)

                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            R.string.register_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            R.string.register_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.btn_register)

                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.server_connection_error, t.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}