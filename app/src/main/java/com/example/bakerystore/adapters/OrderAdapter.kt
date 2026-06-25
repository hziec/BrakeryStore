package com.example.bakerystore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bakerystore.R
import com.example.bakerystore.models.OrderResponse
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(
    private val orders: MutableList<OrderResponse>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvPayment: TextView = itemView.findViewById(R.id.tvPayment)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        holder.tvOrderId.text = "Đơn hàng #${order.orderId}"
        holder.tvStatus.text = order.status ?: "Pending"
        holder.tvCreatedAt.text = "Ngày đặt: ${formatDate(order.createdAt)}"
        holder.tvAddress.text = "Địa chỉ: ${order.shippingAddress ?: "Chưa có"}"
        holder.tvPayment.text =
            "Thanh toán: ${order.paymentMethod ?: "COD"} - ${order.paymentStatus ?: "Pending"}"
        holder.tvTotal.text = "Tổng tiền: ${formatter.format(order.totalAmount)}"
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun setData(newOrders: List<OrderResponse>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    private fun formatDate(date: String?): String {
        if (date.isNullOrEmpty()) return "Không rõ"

        return if (date.length >= 10) {
            date.substring(0, 10)
        } else {
            date
        }
    }
}