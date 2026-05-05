package com.train.pos

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.train.pos.model.MonthlyReportItem
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat // Added import
import java.util.*

object MonthlyPdfGenerator {

    fun export(
        context: Context,
        month: String,
        revenue: Int,
        profit: Int,
        list: List<MonthlyReportItem>
    ): File {

        val pdf = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint().apply {
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
        }

        // Formatter for commas
        val formatter = NumberFormat.getInstance(Locale.US)

        val pageInfo = PdfDocument.PageInfo
            .Builder(595, 842, 1) // A4
            .create()

        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas

        var y = 40

        /* ---------- HEADER ---------- */
        canvas.drawText("Monthly Report", 40f, y.toFloat(), titlePaint)
        y += 30
        canvas.drawText("Month: $month", 40f, y.toFloat(), paint)
        y += 40

        /* ---------- SUMMARY ---------- */
        // Formatted summary numbers
        canvas.drawText("Total Revenue: ${formatter.format(revenue)} Ks", 40f, y.toFloat(), paint)
        y += 20
        canvas.drawText("Total Profit: ${formatter.format(profit)} Ks", 40f, y.toFloat(), paint)
        y += 30

        /* ---------- TABLE HEADER ---------- */
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Product", 40f, y.toFloat(), paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Qty", 230f, y.toFloat(), paint)
        canvas.drawText("Revenue", 350f, y.toFloat(), paint)
        canvas.drawText("Profit", 480f, y.toFloat(), paint)

        y += 15
        paint.typeface = Typeface.DEFAULT

        canvas.drawLine(40f, y.toFloat(), 550f, y.toFloat(), paint)
        y += 20

        /* ---------- TABLE BODY ---------- */
        list.forEach {

            if (y > 800) {
                pdf.finishPage(page)
                page = pdf.startPage(pageInfo)
                canvas = page.canvas
                y = 40
            }

            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(it.productName, 40f, y.toFloat(), paint)

            paint.textAlign = Paint.Align.RIGHT
            // Added formatting here
            canvas.drawText(formatter.format(it.sellingQty), 230f, y.toFloat(), paint)
            canvas.drawText(formatter.format(it.totalRevenue), 350f, y.toFloat(), paint)
            canvas.drawText(formatter.format(it.totalProfit), 480f, y.toFloat(), paint)

            y += 18
        }

        paint.textAlign = Paint.Align.LEFT
        pdf.finishPage(page)

        /* ---------- SAVE ---------- */
        val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val customDir = File(downloadsFolder, "POS_Reports")
        if (!customDir.exists()) {
            customDir.mkdirs()
        }
        val file = File(customDir, "Monthly_Report_$month.pdf")

        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}