package com.train.pos

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.train.pos.model.YearlyChartPoint
import kotlin.math.max

class YearlyBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var data: List<YearlyChartPoint> = emptyList()
    private var progress = 1f

    fun setData(list: List<YearlyChartPoint>) {
        data = list
        startAnimation()
    }

    /* ---------- CONSTANTS ---------- */

    private val topPadding = 50f        // value text space
    private val bottomPadding = 42f     // month text space

    /* ---------- PAINT ---------- */

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 26f
        textAlign = Paint.Align.CENTER
    }
    private val monthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val maxProfit = max(1, data.maxOf { it.profit }.toInt())

        // REAL drawable area
        val chartTop = topPadding
        val chartBottom = height - bottomPadding
        val chartHeight = chartBottom - chartTop

        val barWidth = width / (data.size * 1.4f)
        val space = barWidth * 0.35f
        val radius = barWidth / 2

        data.forEachIndexed { index, item ->

            val left = index * (barWidth + space) + space
            val barHeight =
                (item.profit.toFloat() / maxProfit) * chartHeight * 0.9f * progress

            val top = chartBottom - barHeight
            val right = left + barWidth

            barPaint.shader = LinearGradient(
                0f, top, 0f, chartBottom,
                Color.parseColor("#43A047"),
                Color.parseColor("#A5D6A7"),
                Shader.TileMode.CLAMP
            )

            val rect = RectF(left, top, right, chartBottom)
            canvas.drawRoundRect(rect, radius, radius, barPaint)

            // 💰 Profit value (always visible)
            canvas.drawText(
                item.profit.toString()+"Ks",
                left + barWidth / 2,
                top - 10f,
                valuePaint
            )

            // Month (closer to bar)
            canvas.drawText(
                monthName(item.month),
                left + barWidth / 2,
                height - 10f,
                monthPaint
            )
        }
    }

    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 900
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun monthName(m: Int): String =
        listOf("Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec")[m - 1]

    private fun formatValue(v: Long): String {
        return when {
            v >= 1_000_000 -> String.format("%.1fM", v / 1_000_000f)
            v >= 1_000 -> String.format("%.0fK", v / 1_000f)
            else -> v.toString()
        }
    }
}

