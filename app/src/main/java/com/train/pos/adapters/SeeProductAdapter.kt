package com.train.pos.adapters

import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.CartManager
import com.train.pos.R
import com.train.pos.entries.ProductEntity
import java.io.File

class SeeProductAdapter(private val onItemClick: (ProductEntity) -> Unit) :
    RecyclerView.Adapter<SeeProductAdapter.VH>() {

    private var list = emptyList<ProductEntity>()

    fun submitList(data: List<ProductEntity>) {
        list = data
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvseeName)
        val tvPrice: TextView = view.findViewById(R.id.tvseePrice)
        val imgProduct: ImageView=view.findViewById<ImageView>(R.id.seeproductimage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.seeitem_product, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = list[position]
        holder.tvName.text = product.name
       // holder.tvPrice.text = "${product.price}Ks"
        holder.tvPrice.text = String.format("%,d Ks", product.price)

        if (!product.imageUri.isNullOrEmpty()) {
           // holder.imgProduct.setImageURI(Uri.parse(product.imageUri))
            holder.imgProduct.setImageURI(Uri.fromFile(File(product.imageUri)))
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_foreground)
        }

        //  ITEM CLICK
        holder.itemView.setOnClickListener {
            CartManager.addProduct(product.name, product.price.toInt(),product.costPrice)
            onItemClick(product)
        }
    }

    override fun getItemCount() = list.size
}
