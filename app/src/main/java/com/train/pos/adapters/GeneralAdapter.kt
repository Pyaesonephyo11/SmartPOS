package com.train.pos.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.train.pos.R
import com.train.pos.entries.CategoryEntity
import com.train.pos.model.General

class GeneralAdapter(
    private val list: List<General>,
    private val onClick: (General) -> Unit
) : RecyclerView.Adapter<GeneralAdapter.VH>() {




    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemcategory, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = list[position]
        holder.button.text = category.name

        // selected UI
        holder.button.setBackgroundTintList(
            ColorStateList.valueOf(
                if (category.isSelected)
                    Color.parseColor("#0FA958")
                else
                    Color.parseColor("#E0E0E0")
            )
        )

        holder.button.setTextColor(
            if (category.isSelected) Color.WHITE else Color.BLACK
        )
        holder.button.setOnClickListener {

            list.forEach { it.isSelected = false }
            category.isSelected = true
            notifyDataSetChanged()
            onClick(category)
        }
    }

    override fun getItemCount() = list.size
}