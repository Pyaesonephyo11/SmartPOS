package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.entries.ProductEntity

class ProductAdapter :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val list = mutableListOf<ProductEntity>()

    fun submitList(newList: List<ProductEntity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val product = list[position]
        holder.tvName.text = product.name
        holder.tvPrice.text = "${product.price} Ks"
    }

    override fun getItemCount(): Int = list.size
}
