package com.example.bakerystore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var tvCheckoutTitle: TextView
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

    private lateinit var layoutCheckoutContent: LinearLayout
    private lateinit var layoutCheckoutSuccess: LinearLayout
    private lateinit var tvSuccessMessage: TextView
    private lateinit var btnViewOrderSuccess: Button
    private lateinit var btnGoHomeSuccess: Button

    private lateinit var sessionManager: SessionManager

    private var totalAmount: Double = 0.0
    private var selectedCartItemIds: IntArray = intArrayOf()
    private var currentOrderId: Int = 0

    private var productNames: Array<String> = emptyArray()
    private var productPrices: DoubleArray = doubleArrayOf()
    private var productQuantities: IntArray = intArrayOf()
    private var productTotals: DoubleArray = doubleArrayOf()

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
        receiveSelectedCartItems()
        setupEvents()
    }

    private fun bindViews() {
        tvCheckoutTitle = findViewById(R.id.tvCheckoutTitle)
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

        layoutCheckoutContent = findViewById(R.id.layoutCheckoutContent)
        layoutCheckoutSuccess = findViewById(R.id.layoutCheckoutSuccess)
        tvSuccessMessage = findViewById(R.id.tvSuccessMessage)
        btnViewOrderSuccess = findViewById(R.id.btnViewOrderSuccess)
        btnGoHomeSuccess = findViewById(R.id.btnGoHomeSuccess)
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

        btnViewOrderSuccess.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("ORDER_ID", currentOrderId)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnGoHomeSuccess.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun receiveSelectedCartItems() {
        selectedCartItemIds = intent.getIntArrayExtra("selected_cart_item_ids") ?: intArrayOf()
        productNames = intent.getStringArrayExtra("product_names") ?: emptyArray()
        productPrices = intent.getDoubleArrayExtra("product_prices") ?: doubleArrayOf()
        productQuantities = intent.getIntArrayExtra("product_quantities") ?: intArrayOf()
        productTotals = intent.getDoubleArrayExtra("product_totals") ?: doubleArrayOf()
        totalAmount = intent.getDoubleExtra("total_amount", 0.0)

        if (totalAmount <= 0.0) {
            totalAmount = productTotals.sum()
        }

        if (selectedCartItemIds.isEmpty() || productNames.isEmpty()) {
            tvOrderSummary.text = "Không có sản phẩm nào được chọn"
            tvTotal.text = "Tổng thanh toán: ${currencyFormatter.format(0)}"

            Toast.makeText(
                this,
                "Không có sản phẩm nào được chọn để thanh toán",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        tvTotal.text = "Tổng thanh toán: ${currencyFormatter.format(totalAmount)}"
        tvOrderSummary.text = buildSelectedOrderSummary()
    }

    private fun buildSelectedOrderSummary(): String {
        if (productNames.isEmpty()) {
            return "Sản phẩm: Chưa có sản phẩm\nSố lượng: 0\nGiá: ${currencyFormatter.format(0)}"
        }

        val builder = StringBuilder()
        var totalQuantity = 0

        for (i in productNames.indices) {
            val name = productNames.getOrNull(i) ?: "Sản phẩm"
            val price = productPrices.getOrNull(i) ?: 0.0
            val quantity = productQuantities.getOrNull(i) ?: 1
            val itemTotal = productTotals.getOrNull(i) ?: (price * quantity)

            totalQuantity += quantity

            builder.append("${i + 1}. $name\n")
            builder.append("   Số lượng: $quantity\n")
            builder.append("   Đơn giá: ${currencyFormatter.format(price)}\n")
            builder.append("   Thành tiền: ${currencyFormatter.format(itemTotal)}")

            if (i != productNames.size - 1) {
                builder.append("\n\n")
            }
        }

        builder.append("\n\nTổng số lượng: $totalQuantity")
        builder.append("\nTổng tiền: ${currencyFormatter.format(totalAmount)}")

        return builder.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun handleCheckout() {
        val receiverName = edtReceiverName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val street = edtStreet.text.toString().trim()
        val ward = edtWard.text.toString().trim()
        val district = edtDistrict.text.toString().trim()
        val city = edtCity.text.toString().trim()

        if (receiverName.isEmpty()) {
            edtReceiverName.error = "Vui lòng nhập tên người nhận"
            edtReceiverName.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            edtPhone.error = "Vui lòng nhập số điện thoại"
            edtPhone.requestFocus()
            return
        }

        if (phone.length !in 9..11) {
            edtPhone.error = "Số điện thoại không hợp lệ"
            edtPhone.requestFocus()
            return
        }

        if (street.isEmpty()) {
            edtStreet.error = "Vui lòng nhập địa chỉ"
            edtStreet.requestFocus()
            return
        }

        if (ward.isEmpty()) {
            edtWard.error = "Vui lòng nhập phường/xã"
            edtWard.requestFocus()
            return
        }

        if (district.isEmpty()) {
            edtDistrict.error = "Vui lòng nhập quận/huyện"
            edtDistrict.requestFocus()
            return
        }

        if (city.isEmpty()) {
            edtCity.error = "Vui lòng nhập tỉnh/thành phố"
            edtCity.requestFocus()
            return
        }

        if (selectedCartItemIds.isEmpty()) {
            Toast.makeText(
                this,
                "Không có sản phẩm nào được chọn để đặt hàng",
                Toast.LENGTH_SHORT
            ).show()
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
            paymentMethod = paymentMethod,
            cartItemIds = selectedCartItemIds.toList()
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

                        showCheckoutSuccess(orderId)
                    } else {
                        val errorMessage = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            e.message
                        } ?: "Đặt hàng thất bại"

                        Toast.makeText(
                            this@CheckoutActivity,
                            "Lỗi ${response.code()}: $errorMessage",
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

    @SuppressLint("SetTextI18n")
    private fun showCheckoutSuccess(orderId: Int) {
        currentOrderId = orderId

        tvCheckoutTitle.text = "Hoàn Tất Đơn Hàng"

        tvSuccessMessage.text =
            "Cảm ơn bạn đã ủng hộ Bakery Store.\nMã đơn hàng: #$orderId\nTổng thanh toán: ${currencyFormatter.format(totalAmount)}\nĐơn hàng của bạn đang được xử lý."

        layoutCheckoutContent.visibility = View.GONE
        layoutCheckoutSuccess.visibility = View.VISIBLE
    }
}