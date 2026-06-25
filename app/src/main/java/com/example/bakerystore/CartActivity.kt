package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakerystore.adapters.CartAdapter
import com.example.bakerystore.models.CartResponse
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.UpdateCartItemRequest
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnDeleteSelected: Button
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sessionManager: SessionManager
    private lateinit var cartAdapter: CartAdapter

    private val selectedIds = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_cart)

        bindViews()
        setupRecyclerView()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()
        loadCart()
    }

    private fun bindViews() {
        rvCart = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            items = mutableListOf(),
            selectedIds = selectedIds,
            onQuantityChange = { item, newQuantity ->
                updateQuantity(item.cartItemId, newQuantity)
            },
            onDeleteClick = { item ->
                removeItem(item.cartItemId)
            },
            onSelectionChange = {
                updateTotal()
            }
        )

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter
    }

    private fun setupEvents() {
        btnCheckout.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm cần mua", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        btnDeleteSelected.setOnClickListener {
            val selectedItems = cartAdapter.getSelectedItems()

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Chưa chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedItems.forEach {
                removeItem(it.cartItemId)
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadCart() {
        ApiClient.apiService.getCartByUser(sessionManager.getUserId())
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        cartAdapter.setData(response.body()!!.items)
                        updateTotal()
                    } else {
                        Toast.makeText(
                            this@CartActivity,
                            "Không tải được giỏ hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CartActivity,
                        "Lỗi giỏ hàng: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateQuantity(cartItemId: Int, quantity: Int) {
        ApiClient.apiService.updateCartItem(
            cartItemId,
            UpdateCartItemRequest(quantity)
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    loadCart()
                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "Không cập nhật được số lượng",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(
                    this@CartActivity,
                    "Lỗi cập nhật: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun removeItem(cartItemId: Int) {
        ApiClient.apiService.removeCartItem(cartItemId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        loadCart()
                    } else {
                        Toast.makeText(
                            this@CartActivity,
                            "Không xóa được sản phẩm",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Toast.makeText(
                        this@CartActivity,
                        "Lỗi xóa: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateTotal() {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        val total = cartAdapter.getSelectedItems().sumOf { it.total }

        tvTotal.text = "Tổng tiền đã chọn: ${formatter.format(total)}"
    }
}