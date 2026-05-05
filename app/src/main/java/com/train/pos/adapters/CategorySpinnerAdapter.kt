package com.train.pos.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.train.pos.entries.CategoryEntity

class CategorySpinnerAdapter(
    context: Context,
    private val items: List<CategoryEntity>
) : ArrayAdapter<CategoryEntity>(
    context,
    android.R.layout.simple_spinner_item,
    items
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent) as TextView
        v.text = items[position].name
        return v
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getDropDownView(position, convertView, parent) as TextView
        v.text = items[position].name
        return v
    }
}
