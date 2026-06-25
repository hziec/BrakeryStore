package com.example.bakerystore

import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bakerystore.models.CartResponse
import com.example.bakerystore.models.CheckoutRequest
import com.example.bakerystore.models.CheckoutResponse
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var edtReceiverName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtStreet: EditText
    private lateinit var edtWard: EditText
    private lateinit var edtDistrict: EditText
    private lateinit var edtCity: EditText
    private lateinit var rgPayment: RadioGroup
    private lateinit var btnPlaceOrder: Button
    private lateinit var btnBack: ImageButton

    private lateinit var sessionManager: SessionManager
    private var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_checkout)

        bindViews()
        setupDefaultUserInfo()
        setupEvents()
        loadCartTotal()
    }

    private fun bindViews() {
        tvTotal = findViewById(R.id.tvTotal)
        edtReceiverName = findViewById(R.id.edtReceiverName)
        edtPhone = findViewById(R.id.edtPhone)
        edtStreet = findViewById(R.id.edtStreet)
        edtWard = findViewById(R.id.edtWard)
        edtDistrict = findViewById(R.id.edtDistrict)
        edtCity = findViewById(R.id.edtCity)
        rgPayment = findViewById(R.id.rgPayment)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupDefaultUserInfo() {
        edtReceiverName.setText(sessionManager.getFullName())
        edtPhone.setText(sessionManager.getPhone())
    }

    private fun setupEvents() {
        btnPlaceOrder.setOnClickListener {
            handleCheckout()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadCartTotal() {
        ApiClient.apiService.getCartByUser(sessionManager.getUserId())
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        totalAmount = response.body()!!.totalAmount
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        tvTotal.text = "Tổng thanh toán: ${formatter.format(totalAmount)}"
                    }
                }
                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Toast.makeText(this@CheckoutActivity, "Không tải được tổng tiền", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun handleCheckout() {
        val receiverName = edtReceiverName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val street = edtStreet.text.toString().trim()
        val ward = edtWard.text.toString().trim()
        val district = edtDistrict.text.toString().trim()
        val city = edtCity.text.toString().trim()

        if (receiverName.isEmpty() || phone.isEmpty() || street.isEmpty() || ward.isEmpty() || district.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = when (rgPayment.checkedRadioButtonId) {
            R.id.rbBanking -> "Banking"
            else -> "COD"
        }

        btnPlaceOrder.isEnabled = false
        btnPlaceOrder.text = "Đang xử lý..."

        val request = CheckoutRequest(
            userId = sessionManager.getUserId(),
            receiverName = receiverName,
            phone = phone,
            street = street,
            city = city,
            district = district,
            ward = ward,
            paymentMethod = paymentMethod
        )

        ApiClient.apiService.checkout(request)
            .enqueue(object : Callback<CheckoutResponse> {
                override fun onResponse(call: Call<CheckoutResponse>, response: Response<CheckoutResponse>) {
                    btnPlaceOrder.isEnabled = true
                    btnPlaceOrder.text = "Xác Nhận Đặt Hàng"

                    if (response.isSuccessful && response.body() != null) {
                        val checkoutResponse = response.body()!!
                        val msg = checkoutResponse.message ?: "Đặt hàng thành công"
                        Toast.makeText(this@CheckoutActivity, msg, Toast.LENGTH_SHORT).show()
                        showSuccessDialog(checkoutResponse.orderId)
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    btnPlaceOrder.isEnabled = true
                    btnPlaceOrder.text = "Xác Nhận Đặt Hàng"
                    Toast.makeText(this@CheckoutActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun showSuccessDialog(orderId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnViewOrder).setOnClickListener {
            dialog.dismiss()
            // Chuyển sang xem chi tiết đơn hàng (trong HistoryActivity)
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("ORDER_ID", orderId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        dialogView.findViewById<Button>(R.id.btnGoHome).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}