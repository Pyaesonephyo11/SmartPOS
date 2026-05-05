package com.train.pos.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.model.ProductWithCategory

class ProductManagePagingAdapter(
    private val onEdit: (ProductWithCategory) -> Unit,
    private val onDelete: (ProductWithCategory) -> Unit
) : PagingDataAdapter<ProductWithCategory, ProductManagePagingAdapter.VH>(
    ProductManageDiff
) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvManageName)
        val image: ImageView = v.findViewById(R.id.imageprofit)
        val tvCategory: TextView = v.findViewById(R.id.tvManageCategory)
        val tvPrice: TextView = v.findViewById(R.id.tvManagePrice)
        val tvRev : TextView  = v.findViewById(R.id.tvManageRevenue)
        val btnEdit: ImageButton = v.findViewById(R.id.btnManageEdit)
        val btnDelete: ImageButton = v.findViewById(R.id.btnManageDelete)
        val barview : View = v.findViewById(R.id.barlayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_manage, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: return

        holder.tvName.text = item.productName
        holder.tvCategory.text = item.categoryName
        holder.tvPrice.text = String.format("%,d Ks", item.price)
        val profit = item.price - item.costPrice
        val profitPercent= (profit.toDouble() / item.price.toDouble()) * 100
       // holder.tvRev.text = "${profitPercent}%"
        if (profit >= 0) {
            holder.tvRev.setTextColor(Color.parseColor("#0FA958")) // green
            holder.barview.setBackgroundResource(R.drawable.bg_rev)
            holder.image.setBackgroundResource(R.drawable.outline_bar_chart_24)
            holder.tvRev.text = String.format("%.1f%%", profitPercent)
        } else {
            holder.tvRev.setTextColor(Color.parseColor("#C8361B"))
            holder.barview.setBackgroundResource(R.drawable.bg_lose)
            holder.image.setBackgroundResource(R.drawable.barchartred)
            holder.tvRev.text = String.format("%.1f%%", profitPercent)
        }


        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }
    object ProductManageDiff : DiffUtil.ItemCallback<ProductWithCategory>() {
        override fun areItemsTheSame(
            oldItem: ProductWithCategory,
            newItem: ProductWithCategory
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: ProductWithCategory,
            newItem: ProductWithCategory
        ): Boolean {
            return oldItem == newItem
        }
    }
}



