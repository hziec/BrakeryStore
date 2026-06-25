package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakerystore.adapters.CategoryAdapter
import com.example.bakerystore.adapters.ProductAdapter
import com.example.bakerystore.models.AddToCartRequest
import com.example.bakerystore.models.CategoryResponse
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.ProductResponse
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvHello: TextView
    private lateinit var edtSearch: EditText
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnCart: ImageButton
    private lateinit var btnProfile: ImageButton

    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var sessionManager: SessionManager

    private var selectedCategoryId: Int? = null
    private var currentKeyword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        bindViews()
        setupRecyclerViews()
        setupEvents()

        tvHello.text = "Xin chào, ${sessionManager.getFullName()}"

        loadCategories()
        loadProducts()
    }

    private fun bindViews() {
        tvHello = findViewById(R.id.tvHello)
        edtSearch = findViewById(R.id.edtSearch)
        rvCategories = findViewById(R.id.rvCategories)
        rvProducts = findViewById(R.id.rvProducts)
        btnCart = findViewById(R.id.btnCart)
        btnProfile = findViewById(R.id.btnProfile)
    }

    private fun setupRecyclerViews() {
        categoryAdapter = CategoryAdapter(mutableListOf()) { category ->
            selectedCategoryId = category?.categoryId
            loadProducts()
        }

        rvCategories.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvCategories.adapter = categoryAdapter

        productAdapter = ProductAdapter(
            products = mutableListOf(),
            onViewClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.productId)
                startActivity(intent)
            },
            onCartClick = { product ->
                addToCart(product)
            }
        )

        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = productAdapter
    }

    private fun setupEvents() {
        btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnProfile.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.menu.add("Hồ sơ cá nhân")
            popup.menu.add("Đăng xuất")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Hồ sơ cá nhân" -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    "Đăng xuất" -> {
                        sessionManager.logout()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentKeyword = s?.toString()?.trim()

                if (currentKeyword.isNullOrEmpty()) {
                    currentKeyword = null
                }

                loadProducts()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        })
    }

    private fun loadCategories() {
        ApiClient.apiService.getCategories()
            .enqueue(object : Callback<List<CategoryResponse>> {
                override fun onResponse(
                    call: Call<List<CategoryResponse>>,
                    response: Response<List<CategoryResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        categoryAdapter.setData(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Không tải được danh mục",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<CategoryResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Lỗi danh mục: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun loadProducts() {
        ApiClient.apiService.getProducts(
            categoryId = selectedCategoryId,
            keyword = currentKeyword
        ).enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(
                call: Call<List<ProductResponse>>,
                response: Response<List<ProductResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    productAdapter.setData(response.body()!!)
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "Không tải được sản phẩm",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                Toast.makeText(
                    this@HomeActivity,
                    "Lỗi sản phẩm: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun addToCart(product: ProductResponse) {
        val userId = sessionManager.getUserId()

        if (userId <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.addToCart(
            AddToCartRequest(
                userId = userId,
                productId = product.productId,
                quantity = 1
            )
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@HomeActivity,
                        "Đã thêm ${product.productName} vào giỏ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "Không thể thêm vào giỏ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(
                    this@HomeActivity,
                    "Lỗi giỏ hàng: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}