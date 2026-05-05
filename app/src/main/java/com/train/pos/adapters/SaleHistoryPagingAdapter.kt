package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.TransactionDetailDialog
import com.train.pos.entries.SaleItemEntity
import com.train.pos.model.SaleWithItems
import com.train.pos.printer.PrinterHelper
import java.text.SimpleDateFormat
import java.util.Date


class SaleHistoryPagingAdapter(private val onItemClick: (SaleWithItems) -> Unit) : PagingDataAdapter<SaleWithItems, SaleHistoryPagingAdapter.VH>(Diff()) {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvItemsSummary: TextView = v.findViewById(R.id.tvItems)
        val tvTotalAmount: TextView = v.findViewById(R.id.tvTotal)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val tvQtys: TextView = v.findViewById(R.id.tvQtys)
        val btn: Button = v.findViewById(R.id.btnReprint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sale_history, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val saleWithItems = getItem(position) ?: return
        val sale = saleWithItems.sale

        holder.tvDate.text = SimpleDateFormat("MM/dd/yyyy, hh:mm a").format(Date(sale.dateTime))

        holder.tvStatus.text = sale.status
        holder.tvTotalAmount.text = String.format("%,d Ks", sale.totalAmount)

        holder.tvItemsSummary.text = buildItemSummary(saleWithItems.items)

        val totalQty = saleWithItems.items.sumOf { it.qty }
        holder.tvQtys.text = "$totalQty Items"
        holder.itemView.setOnClickListener { onItemClick(saleWithItems) }


        holder.btn.setOnClickListener {
            onItemClick(saleWithItems)
        }


    }

    private fun buildItemSummary(items: List<SaleItemEntity>): String =
        items.joinToString(", ") { "${it.qty}x ${it.productName}" }

    class Diff : DiffUtil.ItemCallback<SaleWithItems>() {
        override fun areItemsTheSame(a: SaleWithItems, b: SaleWithItems) =
            a.sale.saleId == b.sale.saleId

        override fun areContentsTheSame(a: SaleWithItems, b: SaleWithItems) =
            a == b
    }
}


