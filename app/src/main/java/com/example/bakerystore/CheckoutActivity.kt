package com.example.bakerystore

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

@Suppress("DEPRECATION")
class CheckoutActivity : AppCompatActivity() {

    private lateinit var tvTotal: TextView
    private lateinit var tvOrderSummary: TextView

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

    private val currencyFormatter: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

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
        tvOrderSummary = findViewById(R.id.tvOrderSummary)

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

    @SuppressLint("SetTextI18n")
    private fun loadCartTotal() {
        tvOrderSummary.text = "Đang tải thông tin đơn hàng..."

        ApiClient.apiService.getCartByUser(sessionManager.getUserId())
            .enqueue(object : Callback<CartResponse> {

                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val cartResponse = response.body()!!

                        totalAmount = cartResponse.totalAmount
                        tvTotal.text = "Tổng thanh toán: ${currencyFormatter.format(totalAmount)}"

                        tvOrderSummary.text = buildOrderSummary(cartResponse)
                    } else {
                        tvOrderSummary.text = "Không tải được thông tin đơn hàng"

                        Toast.makeText(
                            this@CheckoutActivity,
                            "Không tải được tổng tiền",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    tvOrderSummary.text = "Không tải được thông tin đơn hàng"

                    Toast.makeText(
                        this@CheckoutActivity,
                        "Không tải được tổng tiền: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun buildOrderSummary(cartResponse: CartResponse): String {
        val items = getCartItemsByReflection(cartResponse)

        if (items.isEmpty()) {
            return "Sản phẩm: Chưa có sản phẩm\nSố lượng: 0\nGiá: ${currencyFormatter.format(0)}"
        }

        val builder = StringBuilder()
        var totalQuantity = 0

        items.forEachIndexed { index, item ->
            val productName = getValueFromObject(
                item,
                listOf("productName", "name", "productTitle", "title")
            )?.toString() ?: "Sản phẩm"

            val quantity = getValueFromObject(
                item,
                listOf("quantity", "qty", "soLuong")
            )?.toString()?.toIntOrNull() ?: 0

            val priceValue = getValueFromObject(
                item,
                listOf("price", "unitPrice", "productPrice", "gia")
            )?.toString()?.toDoubleOrNull() ?: 0.0

            val totalItemPrice = getValueFromObject(
                item,
                listOf("totalPrice", "subTotal", "subtotal", "amount")
            )?.toString()?.toDoubleOrNull() ?: (priceValue * quantity)

            totalQuantity += quantity

            builder.append("${index + 1}. $productName\n")
            builder.append("   Số lượng: $quantity\n")
            builder.append("   Giá: ${currencyFormatter.format(totalItemPrice)}")

            if (index != items.size - 1) {
                builder.append("\n\n")
            }
        }

        builder.append("\n\nTổng số lượng: $totalQuantity")
        builder.append("\nTổng tiền: ${currencyFormatter.format(totalAmount)}")

        return builder.toString()
    }

    private fun getCartItemsByReflection(cartResponse: CartResponse): List<Any> {
        val possibleFieldNames = listOf(
            "items",
            "cartItems",
            "cartDetails",
            "details",
            "products"
        )

        for (fieldName in possibleFieldNames) {
            val value = getValueFromObject(cartResponse, listOf(fieldName))

            if (value is List<*>) {
                return value.filterNotNull()
            }
        }

        return emptyList()
    }

    private fun getValueFromObject(obj: Any, possibleNames: List<String>): Any? {
        for (name in possibleNames) {
            try {
                val field = obj.javaClass.declaredFields.firstOrNull {
                    it.name.equals(name, ignoreCase = true)
                }

                if (field != null) {
                    field.isAccessible = true
                    return field.get(obj)
                }
            } catch (_: Exception) {
            }

            try {
                val methodName = "get" + name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                val method = obj.javaClass.methods.firstOrNull {
                    it.name.equals(methodName, ignoreCase = true)
                }

                if (method != null) {
                    return method.invoke(obj)
                }
            } catch (_: Exception) {
            }
        }

        return null
    }

    @SuppressLint("SetTextI18n")
    private fun handleCheckout() {
        val receiverName = edtReceiverName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val street = edtStreet.text.toString().trim()
        val ward = edtWard.text.toString().trim()
        val district = edtDistrict.text.toString().trim()
        val city = edtCity.text.toString().trim()

        if (
            receiverName.isEmpty() ||
            phone.isEmpty() ||
            street.isEmpty() ||
            ward.isEmpty() ||
            district.isEmpty() ||
            city.isEmpty()
        ) {
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

                override fun onResponse(
                    call: Call<CheckoutResponse>,
                    response: Response<CheckoutResponse>
                ) {
                    btnPlaceOrder.isEnabled = true
                    btnPlaceOrder.text = "Xác Nhận Đặt Hàng"

                    if (response.isSuccessful) {
                        val checkoutResponse = response.body()

                        val msg = checkoutResponse?.message ?: "Đặt hàng thành công"
                        val orderId = checkoutResponse?.orderId ?: 0

                        Toast.makeText(
                            this@CheckoutActivity,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()

                        showSuccessDialog(orderId)
                    } else {
                        val errorMessage = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            null
                        } ?: "Đặt hàng thất bại"

                        Toast.makeText(
                            this@CheckoutActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                    btnPlaceOrder.isEnabled = true
                    btnPlaceOrder.text = "Xác Nhận Đặt Hàng"

                    Toast.makeText(
                        this@CheckoutActivity,
                        "Lỗi kết nối: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun showSuccessDialog(orderId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnViewOrder).setOnClickListener {
            dialog.dismiss()

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

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}