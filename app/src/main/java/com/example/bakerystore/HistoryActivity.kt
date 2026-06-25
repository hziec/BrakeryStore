package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakerystore.adapters.OrderAdapter
import com.example.bakerystore.models.OrderResponse
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.ceil

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var tvPage: TextView
    private lateinit var btnBack: android.widget.ImageButton

    private lateinit var sessionManager: SessionManager
    private lateinit var orderAdapter: OrderAdapter

    private val allOrders = mutableListOf<OrderResponse>()
    private var currentPage = 1
    private val pageSize = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_history)

        bindViews()
        setupRecyclerView()
        setupEvents()
        loadOrders()
    }

    private fun bindViews() {
        rvOrders = findViewById(R.id.rvOrders)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        tvPage = findViewById(R.id.tvPage)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(mutableListOf())
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = orderAdapter
    }

    private fun setupEvents() {
        btnPrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                renderPage()
            }
        }

        btnNext.setOnClickListener {
            if (currentPage < getTotalPage()) {
                currentPage++
                renderPage()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadOrders() {
        ApiClient.apiService.getOrdersByUser(sessionManager.getUserId())
            .enqueue(object : Callback<List<OrderResponse>> {
                override fun onResponse(
                    call: Call<List<OrderResponse>>,
                    response: Response<List<OrderResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        allOrders.clear()
                        allOrders.addAll(response.body()!!.sortedByDescending { it.orderId })

                        currentPage = 1
                        renderPage()
                    } else {
                        Toast.makeText(
                            this@HistoryActivity,
                            "Không tải được lịch sử đơn hàng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Lỗi lịch sử: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderPage() {
        val totalPage = getTotalPage()

        if (allOrders.isEmpty()) {
            orderAdapter.setData(emptyList())
            tvPage.text = "Chưa có đơn hàng"
            btnPrev.isEnabled = false
            btnNext.isEnabled = false
            return
        }

        val fromIndex = (currentPage - 1) * pageSize
        val toIndex = minOf(fromIndex + pageSize, allOrders.size)

        val pageData = allOrders.subList(fromIndex, toIndex)

        orderAdapter.setData(pageData)

        tvPage.text = "Trang $currentPage/$totalPage"
        btnPrev.isEnabled = currentPage > 1
        btnNext.isEnabled = currentPage < totalPage
    }

    private fun getTotalPage(): Int {
        if (allOrders.isEmpty()) return 1
        return ceil(allOrders.size / pageSize.toDouble()).toInt()
    }
}