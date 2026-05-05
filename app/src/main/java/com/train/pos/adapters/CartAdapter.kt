package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.model.CartItem

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onQtyChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
  // private val onQtyChanged: () -> Unit  inc dec
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQty)

      // inc dec
      val btnPlus: TextView = itemView.findViewById(R.id.btnPlus)
      val btnMinus: TextView = itemView.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name
       // holder.tvPrice.text = "${item.price} Ks"
        holder.tvPrice.text = String.format("%,d Ks", item.price)
        holder.tvQty.text = "${item.qty}"

        //inc dec
        holder.btnPlus.setOnClickListener {
            item.qty += 1
            holder.tvQty.text = item.qty.toString()
            onQtyChanged()
        }
        holder.btnMinus.setOnClickListener {
            if (item.qty > 1) {
                item.qty -= 1
                holder.tvQty.text = item.qty.toString()
                onQtyChanged()
            } else {
                // qty == 1 အမှန်ပြင်ချင်ရင် delete လုပ်ချင်ရင်
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {

                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                    notifyItemRangeChanged(pos, items.size)
                    onQtyChanged()
                }
            }
            }
    }

    override fun getItemCount(): Int = items.size
}