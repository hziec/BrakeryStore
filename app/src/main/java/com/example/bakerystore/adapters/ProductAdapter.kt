package com.example.bakerystore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakerystore.R
import com.example.bakerystore.models.ProductResponse
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: MutableList<ProductResponse>,
    private val onViewClick: (ProductResponse) -> Unit,
    private val onCartClick: (ProductResponse) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnAddCart: ImageButton = itemView.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        holder.tvProductName.text = product.productName
        holder.tvCategory.text = product.categoryName ?: "Chưa phân loại"
        holder.tvRating.text = "⭐ ${String.format("%.1f", product.averageRating)} (${product.reviewCount})"
        holder.tvPrice.text = formatter.format(product.price)

        // Map product names to high-quality images using utility class
        val displayImageUrl = com.example.bakerystore.utils.ProductImageUtils.getProductImageUrl(
            product.productName,
            product.imageUrl
        )

        Glide.with(holder.itemView.context)
            .load(displayImageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgProduct)

        holder.btnView.setOnClickListener {
            onViewClick(product)
        }

        holder.btnAddCart.setOnClickListener {
            onCartClick(product)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun setData(newProducts: List<ProductResponse>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}