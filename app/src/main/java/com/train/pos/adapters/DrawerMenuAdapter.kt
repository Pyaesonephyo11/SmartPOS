package com.train.pos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.train.pos.R
import com.train.pos.model.DrawerMenu

class DrawerMenuAdapter(
    private val list: List<DrawerMenu>,
    private val onClick: (DrawerMenu) -> Unit
) : RecyclerView.Adapter<DrawerMenuAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.imgIcon)
        val title: TextView = v.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) =
        VH(LayoutInflater.from(p.context)
            .inflate(R.layout.left_drawer_menu, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = list[pos]
        h.icon.setImageResource(item.icon)
        h.title.text = item.title
        h.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = list.size
}
