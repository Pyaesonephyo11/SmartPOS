package com.train.pos.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.ChartItemView
import com.train.pos.model.DailyProfit

class MonthlyChartAdapter :
    ListAdapter<DailyProfit, MonthlyChartAdapter.VH>(
        object : DiffUtil.ItemCallback<DailyProfit>() {
            override fun areItemsTheSame(a: DailyProfit, b: DailyProfit) = a.day == b.day
            override fun areContentsTheSame(a: DailyProfit, b: DailyProfit) = a == b
        }) {

    inner class VH(val view: ChartItemView) :
        RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ChartItemView(parent.context))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.view.setData(getItem(position))
    }
}
