package com.train.pos.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

object MyanmarTextBitmapHelper {

    fun textToBitmap(
        context: Context,
        text: String,
        textSize: Float = 32f,
        width: Int = 576   // 80mm printer
    ): Bitmap {

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            this.textSize = textSize
            typeface = Typeface.createFromAsset(
                context.assets,
                "font/notosansmyanmar.ttf"
            )
        }

        val lines = text.split("\n")
        val lineHeight = paint.fontSpacing
        val height = (lineHeight * lines.size + 20).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        var y = lineHeight
        lines.forEach {
            canvas.drawText(it, 0f, y, paint)
            y += lineHeight
        }

        return bitmap
    }
}
