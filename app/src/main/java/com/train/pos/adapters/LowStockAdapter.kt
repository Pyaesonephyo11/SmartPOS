package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.entries.ProductEntity

class LowStockAdapter( private val onClick: (ProductEntity) -> Unit): ListAdapter<ProductEntity, LowStockAdapter.VH>(diff) {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(R.id.tvName)
        val qty = v.findViewById<TextView>(R.id.tvQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_low_stock, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.name.text = item.name
        holder.qty.text = "${item.stock} LEFT"

        holder.itemView.setOnClickListener {
            onClick(item)
        }

    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(a: ProductEntity, b: ProductEntity) = a.id == b.id
            override fun areContentsTheSame(a: ProductEntity, b: ProductEntity) = a == b
        }
    }
}
