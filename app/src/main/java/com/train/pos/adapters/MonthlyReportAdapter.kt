package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.model.MonthlyReportItem

class MonthlyReportAdapter : RecyclerView.Adapter<MonthlyReportAdapter.VH>() {

    private val list = mutableListOf<MonthlyReportItem>()

    fun submit(data: List<MonthlyReportItem>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvName)
        val qty: TextView = v.findViewById(R.id.tvQty)
        val revenue: TextView = v.findViewById(R.id.tvRevenue)
        val profit: TextView = v.findViewById(R.id.tvProfit)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        val view = LayoutInflater.from(p.context)
            .inflate(R.layout.item_monthly_report, p, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val item = list[i]
        h.name.text = item.productName
        h.qty.text = "${item.sellingQty}"
        h.revenue.text = "${item.totalRevenue}Ks"
        h.profit.text = "${item.totalProfit}Ks"
    }

    override fun getItemCount() = list.size
}
