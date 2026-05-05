package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.entries.SaleItemEntity

class TransactionItemAdapter(
    private val items: List<SaleItemEntity>
) : RecyclerView.Adapter<TransactionItemAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvRPName)
        val qty: TextView = v.findViewById(R.id.tvRPQty)
        val price: TextView = v.findViewById(R.id.tvRPPrice)
        val total: TextView = v.findViewById(R.id.tvRPTotal)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val view = LayoutInflater.from(p.context)
            .inflate(R.layout.item_transaction_product, p, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val i = items[pos]
        h.name.text = i.productName
        h.qty.text = i.qty.toString()
        h.price.text = String.format("%,d Ks", i.price)

        h.total.text = String.format("%,d Ks", i.total)
    }

    override fun getItemCount() = items.size
}

