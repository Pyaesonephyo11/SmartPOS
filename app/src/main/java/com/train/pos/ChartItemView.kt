package com.train.pos

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.train.pos.model.DailyProfit

class ChartItemView(context: Context) : View(context) {

    private var profit = 0
    private var day: String=""
    private val paint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 6f
    }

    fun setData(item: DailyProfit) {
        profit = item.profit
        day = item.day
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val h = height.toFloat()
        val barHeight = (profit / 1000f).coerceAtMost(h)

        canvas.drawLine(
            width / 2f,
            h,
            width / 2f,
            h - barHeight,
            paint
        )
    }
}
