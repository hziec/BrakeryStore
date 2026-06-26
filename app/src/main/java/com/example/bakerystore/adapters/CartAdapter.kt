package com.example.bakerystore.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakerystore.R
import com.example.bakerystore.models.CartItemResponse
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val items: MutableList<CartItemResponse>,
    private val selectedIds: MutableSet<Int>,
    private val onQuantityChange: (CartItemResponse, Int) -> Unit,
    private val onDeleteClick: (CartItemResponse) -> Unit,
    private val onSelectionChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbSelect: CheckBox = itemView.findViewById(R.id.cbSelect)
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        holder.tvProductName.text = item.productName
        holder.tvPrice.text = formatter.format(item.price)
        holder.tvQuantity.text = item.quantity.toString()

        holder.cbSelect.setOnCheckedChangeListener(null)
        holder.cbSelect.isChecked = selectedIds.contains(item.cartItemId)

        holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedIds.add(item.cartItemId)
            } else {
                selectedIds.remove(item.cartItemId)
            }

            onSelectionChange()
        }

        val displayImageUrl = com.example.bakerystore.utils.ProductImageUtils.getProductImageUrl(
            item.productName,
            item.imageUrl
        )

        Glide.with(holder.itemView.context)
            .load(displayImageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.imgProduct)

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                onQuantityChange(item, item.quantity - 1)
            }
        }

        holder.btnPlus.setOnClickListener {
            onQuantityChange(item, item.quantity + 1)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newItems: List<CartItemResponse>) {
        val currentItemIds = newItems.map { it.cartItemId }.toSet()

        selectedIds.retainAll(currentItemIds)

        items.clear()
        items.addAll(newItems)

        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<CartItemResponse> {
        return items.filter { selectedIds.contains(it.cartItemId) }
    }
}