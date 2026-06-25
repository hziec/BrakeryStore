package com.example.bakerystore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bakerystore.R
import com.example.bakerystore.models.CategoryResponse

class CategoryAdapter(
    private val categories: MutableList<CategoryResponse>,
    private val onCategoryClick: (CategoryResponse?) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryItem: TextView = itemView.findViewById(R.id.tvCategoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)

        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.tvCategoryItem.text = category.categoryName
        holder.tvCategoryItem.alpha = if (position == selectedPosition) 1.0f else 0.55f

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()

            if (category.categoryId == 0) {
                onCategoryClick(null)
            } else {
                onCategoryClick(category)
            }
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun setData(newCategories: List<CategoryResponse>) {
        categories.clear()
        categories.add(
            CategoryResponse(
                categoryId = 0,
                categoryName = "Tất cả",
                description = ""
            )
        )
        categories.addAll(newCategories)
        selectedPosition = 0
        notifyDataSetChanged()
    }
}