package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerystore.models.AuthResponse
import com.example.bakerystore.models.LoginRequest
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoRegister: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoRegister = findViewById(R.id.tvGoRegister)

        btnLogin.setOnClickListener {
            handleLogin()
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

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

        if (password.isEmpty()) {
            edtPassword.error = getString(R.string.error_empty_password)
            edtPassword.requestFocus()
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = getString(R.string.login_loading)

        ApiClient.apiService.login(LoginRequest(email, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.btn_login)

                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!

                        sessionManager.saveUser(
                            user.userId,
                            user.fullName,
                            user.email,
                            user.phone,
                            user.role
                        )

                        Toast.makeText(
                            this@LoginActivity,
                            R.string.login_success,
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.login_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.btn_login)

                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.server_connection_error, t.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}