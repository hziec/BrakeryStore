package com.example.bakerystore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakerystore.adapters.ProductAdapter
import com.example.bakerystore.models.AddToCartRequest
import com.example.bakerystore.models.MessageResponse
import com.example.bakerystore.models.ProductResponse
import com.example.bakerystore.utils.ApiClient
import com.example.bakerystore.utils.ProductImageUtils
import com.example.bakerystore.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imgProductLarge: ImageView
    private lateinit var tvProductNameDetail: TextView
    private lateinit var tvPriceDetail: TextView
    private lateinit var tvDescriptionDetail: TextView
    private lateinit var tvQuantityDetail: TextView
    private lateinit var btnMinusDetail: ImageButton
    private lateinit var btnPlusDetail: ImageButton
    private lateinit var btnAddToCartDetail: Button
    private lateinit var btnFavorite: ImageButton
    private lateinit var rvRelatedProducts: RecyclerView
    private lateinit var toolbar: Toolbar

    private lateinit var sessionManager: SessionManager
    private lateinit var relatedAdapter: ProductAdapter

    private var productId: Int = 0
    private var quantity: Int = 1
    private var currentProduct: ProductResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_product_detail)

        productId = intent.getIntExtra("PRODUCT_ID", 0)

        bindViews()
        setupToolbar()
        setupRecyclerView()
        setupEvents()
        loadProductDetail()
    }

    private fun bindViews() {
        imgProductLarge = findViewById(R.id.imgProductLarge)
        tvProductNameDetail = findViewById(R.id.tvProductNameDetail)
        tvPriceDetail = findViewById(R.id.tvPriceDetail)
        tvDescriptionDetail = findViewById(R.id.tvDescriptionDetail)
        tvQuantityDetail = findViewById(R.id.tvQuantityDetail)
        btnMinusDetail = findViewById(R.id.btnMinusDetail)
        btnPlusDetail = findViewById(R.id.btnPlusDetail)
        btnAddToCartDetail = findViewById(R.id.btnAddToCartDetail)
        btnFavorite = findViewById(R.id.btnFavorite)
        rvRelatedProducts = findViewById(R.id.rvRelatedProducts)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        relatedAdapter = ProductAdapter(
            products = mutableListOf(),
            onViewClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.productId)
                startActivity(intent)
                finish()
            },
            onCartClick = { product ->
                addToCart(product.productId, 1)
            }
        )
        rvRelatedProducts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvRelatedProducts.adapter = relatedAdapter
    }

    private fun setupEvents() {
        btnMinusDetail.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantityDetail.text = quantity.toString()
            }
        }

        btnPlusDetail.setOnClickListener {
            val stock = currentProduct?.stock ?: 10
            if (quantity < stock) {
                quantity++
                tvQuantityDetail.text = quantity.toString()
            } else {
                Toast.makeText(this, "Số lượng đã đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddToCartDetail.setOnClickListener {
            currentProduct?.let {
                addToCart(it.productId, quantity)
            }
        }

        btnFavorite.setOnClickListener {
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích ❤️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProductDetail() {
        if (productId <= 0) {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        ApiClient.apiService.getProductById(productId)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        currentProduct = response.body()
                        renderProduct(response.body()!!)
                        loadRelatedProducts(response.body()!!.categoryId)
                    } else {
                        Toast.makeText(this@ProductDetailActivity, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Toast.makeText(this@ProductDetailActivity, "Lỗi server: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun renderProduct(product: ProductResponse) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        tvProductNameDetail.text = product.productName
        tvPriceDetail.text = formatter.format(product.price)
        tvDescriptionDetail.text = product.description ?: "Sản phẩm tươi ngon mỗi ngày được chế biến từ nguyên liệu tự nhiên, mang lại hương vị ngọt ngào khó quên."

        val displayImageUrl = ProductImageUtils.getProductImageUrl(
            product.productName,
            product.imageUrl
        )

        Glide.with(this)
            .load(displayImageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(imgProductLarge)
    }

    private fun loadRelatedProducts(categoryId: Int?) {
        if (categoryId == null) return

        ApiClient.apiService.getProducts(categoryId = categoryId)
            .enqueue(object : Callback<List<ProductResponse>> {
                override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val filteredList = response.body()!!.filter { it.productId != productId }
                        relatedAdapter.setData(filteredList)
                    }
                }
                override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {}
            })
    }

    private fun addToCart(id: Int, qty: Int) {
        ApiClient.apiService.addToCart(
            AddToCartRequest(userId = sessionManager.getUserId(), productId = id, quantity = qty)
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "Đã thêm $qty sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(this@ProductDetailActivity, "Lỗi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}