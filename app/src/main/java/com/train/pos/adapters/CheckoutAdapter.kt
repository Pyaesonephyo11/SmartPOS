package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.CartManager
import com.train.pos.R
import com.train.pos.model.CartItem

class CheckoutAdapter(
    private val items: List<CartItem>
) : RecyclerView.Adapter<CheckoutAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvName)
        val qty: TextView = v.findViewById(R.id.tvQty)
        val price: TextView = v.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, p: Int) {
        val item = items[p]
        val price=String.format("%,d Ks", item.price)
        val totalAmt: Int=item.qty * item.price
        h.name.text = item.name
        h.qty.text = "${item.qty} x $price"
        h.price.text =String.format("%,d Ks", totalAmt)
    }

    override fun getItemCount() = items.size
}
