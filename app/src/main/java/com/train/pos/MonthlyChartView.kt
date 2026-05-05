package com.train.pos

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.train.pos.model.DailyProfit
import kotlin.math.abs
import kotlin.math.max

class MonthlyChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    /* ---------------- DATA ---------------- */

    private var data: List<DailyProfit> = emptyList()
    private var touchedIndex: Int? = null

    fun setData(list: List<DailyProfit>) {
        data = list.sortedBy { it.day }
        invalidate()
    }

    /* ---------------- SIZE ---------------- */

    private val paddingLeft = 90f
    private val paddingRight = 40f
    private val paddingTop = 40f
    private val paddingBottom = 80f

    /* ---------------- PAINT ---------------- */

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2ECC71") // GREEN
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2ECC71")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0E0E0")
        strokeWidth = 2f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 26f
    }

    private val tooltipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(10f, 0f, 4f, Color.argb(80, 0, 0, 0))
    }

    /* ---------------- TOOLTIP SIZE ---------------- */

    private val tooltipWidth = 220f
    private val tooltipHeight = 90f
    private val arrowHeight = 14f
    private val arrowWidth = 22f

    /* ---------------- DRAW ---------------- */

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom
        if (chartWidth <= 0 || chartHeight <= 0) return

        val maxValue = max(data.maxOf { it.profit }, 1)
        val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)

        /* ---------- GRID + Y LABEL ---------- */
        for (i in 0..4) {
            val y = paddingTop + chartHeight * i / 4
            canvas.drawLine(
                paddingLeft,
                y,
                width - paddingRight,
                y,
                gridPaint
            )

            val value = maxValue - (maxValue * i / 4)
            canvas.drawText(value.toString(), 20f, y + 8f, textPaint)
        }

        /* ---------- LINE ---------- */
        val path = Path()

        data.forEachIndexed { index, item ->
            val x = paddingLeft + index * stepX
            val y = paddingTop + chartHeight -
                    (item.profit.toFloat() / maxValue) * chartHeight

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            canvas.drawCircle(x, y, 6f, pointPaint)
        }

        canvas.drawPath(path, linePaint)

        /* ---------- TOOLTIP ---------- */
        touchedIndex?.let { index ->
            val item = data[index]

            val pointX = paddingLeft + index * stepX
            val pointY = paddingTop + chartHeight -
                    (item.profit.toFloat() / maxValue) * chartHeight

            var tooltipX = pointX - tooltipWidth / 2
            tooltipX = tooltipX.coerceIn(10f, width - tooltipWidth - 10f)

            val showAbove =
                pointY - tooltipHeight - arrowHeight - 12f > paddingTop

            val tooltipY =
                if (showAbove) {
                    pointY - tooltipHeight - arrowHeight - 12f
                } else {
                    pointY + arrowHeight + 12f
                }

            val rect = RectF(
                tooltipX,
                tooltipY,
                tooltipX + tooltipWidth,
                tooltipY + tooltipHeight
            )

            setLayerType(LAYER_TYPE_SOFTWARE, tooltipPaint)
            canvas.drawRoundRect(rect, 18f, 18f, tooltipPaint)

            canvas.drawText(
                "Day ${item.day}",
                rect.left + 20f,
                rect.top + 34f,
                textPaint
            )

            canvas.drawText(
                "Profit: ${item.profit} Ks",
                rect.left + 20f,
                rect.top + 68f,
                textPaint
            )

            val arrowX = pointX.coerceIn(rect.left + 30f, rect.right - 30f)

            val arrowPath = Path().apply {
                if (showAbove) {
                    moveTo(arrowX, rect.bottom)
                    lineTo(arrowX - arrowWidth / 2, rect.bottom + arrowHeight)
                    lineTo(arrowX + arrowWidth / 2, rect.bottom + arrowHeight)
                } else {
                    moveTo(arrowX, rect.top)
                    lineTo(arrowX - arrowWidth / 2, rect.top - arrowHeight)
                    lineTo(arrowX + arrowWidth / 2, rect.top - arrowHeight)
                }
                close()
            }

            canvas.drawPath(arrowPath, tooltipPaint)
        }

    }

    /* ---------------- TOUCH ---------------- */

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (data.isEmpty()) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val chartWidth = width - paddingLeft - paddingRight
                val stepX = chartWidth / (data.size - 1).coerceAtLeast(1)

                val index = ((event.x - paddingLeft) / stepX)
                    .toInt()
                    .coerceIn(0, data.size - 1)

                touchedIndex = index
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                touchedIndex = null
                invalidate()
            }
        }
        return true
    }
}