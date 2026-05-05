package com.train.pos.adapters

import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.CartManager
import com.train.pos.R
import com.train.pos.entries.ProductEntity
import java.io.File

class SeeProductPagingAdapter(private val onItemClick: (ProductEntity) -> Unit) :
    PagingDataAdapter<ProductEntity, SeeProductPagingAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(old: ProductEntity, new: ProductEntity) =
                old.id == new.id

            override fun areContentsTheSame(old: ProductEntity, new: ProductEntity) =
                old == new
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvseeName)
        val price: TextView = view.findViewById(R.id.tvseePrice)
        val image: ImageView = view.findViewById(R.id.seeproductimage)
        val lowStock: TextView = view.findViewById(R.id.tvLowStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.seeitem_product, parent, false)
        return VH(view)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = getItem(position) ?: return
        holder.name.text = product.name
        holder.price.text= String.format("%,d Ks", product.price)
        if (!product.imageUri.isNullOrEmpty()) {
            holder.image.setImageURI(Uri.fromFile(File(product.imageUri)))
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground)
        }

        Log.d("Product Data:",product.barcode.toString())
        holder.lowStock.visibility = View.GONE
        holder.itemView.isEnabled = true
        when {
            product.stock <= 0 -> {
                //  Out of stock
                holder.lowStock.visibility = View.VISIBLE
                holder.lowStock.text = "Out of Stock"

                holder.itemView.isEnabled = false
            }
            product.stock in 1..3 -> {
                // Low stock
                holder.lowStock.visibility = View.VISIBLE
                holder.lowStock.text = "Low Stock (${product.stock})"
            }
        }
        holder.itemView.setOnClickListener {
            if (product.stock > 0) {
                CartManager.addProduct(product.name, product.price.toInt(),product.id,product.costPrice)
                onItemClick(product)
            }
        }
    }
}
