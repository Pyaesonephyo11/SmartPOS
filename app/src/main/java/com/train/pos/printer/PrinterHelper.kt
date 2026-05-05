package com.train.pos.printer

import android.content.Context
import android.graphics.*
import android.widget.Toast
import com.train.pos.PrinterManager
import com.train.pos.ShopPref
import com.train.pos.model.SaleWithItems
import java.text.SimpleDateFormat
import java.util.*

object PrinterHelper {

    fun printSale(context: Context, sale: SaleWithItems) {
        if (!PrinterManager.isConnected()) {
            Toast.makeText(context, "Printer not connected", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val shop = ShopPref.load(context)
                val width = 384 // Standard 58mm printer width

                // 1. Generate the Bitmap (Renders Myanmar + English fonts)
                val bitmap = createSaleBitmap(context, shop, sale, width)

                // 2. Send the Bitmap to the hardware
                PrinterManager.reset()
                PrinterManager.printBitmap(bitmap)
                PrinterManager.feed(3) // Space to tear off

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createSaleBitmap(
        context: Context,
        shop: com.train.pos.model.ShopInfo, // Ensure this matches your model name
        sale: SaleWithItems,
        width: Int
    ): Bitmap {

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            isAntiAlias = true
            try {
                // Ensure pyidaungsu.ttf is in app/src/main/assets/fonts/
                typeface = Typeface.createFromAsset(context.assets, "fonts/pyidaungsu.ttf")
            } catch (e: Exception) {
                typeface = Typeface.DEFAULT
            }
        }

        val headerPaint = Paint(paint).apply {
            textSize = 24f
            typeface = Typeface.create(paint.typeface, Typeface.BOLD)
        }

        // Calculate height dynamically based on item count
        val height = 350 + (sale.items.size * 35) + 250
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        var y = 40f
        val centerX = width / 2f
        val rightX = (width - 10).toFloat()

        // --- Header (Shop Info) ---
        headerPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(shop.shopName, centerX, y, headerPaint)
        y += 30
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(shop.address, centerX, y, paint)
        y += 25
        canvas.drawText(shop.phone, centerX, y, paint)
        y += 40

        // --- Sale Info ---
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Inv: ${sale.sale.invoiceNo}", 10f, y, paint)
        y += 25
        val dateStr = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(sale.sale.dateTime))
        canvas.drawText("Date: $dateStr", 10f, y, paint)
        y += 20
        canvas.drawLine(10f, y, rightX, y, paint)
        y += 35

        // --- Items List ---
        sale.items.forEach { item ->
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(item.productName, 10f, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            val itemDetail = "${item.qty} x ${format(item.price)}"
            canvas.drawText(itemDetail, rightX, y, paint)
            y += 35
        }

        y += 5
        canvas.drawLine(10f, y, rightX, y, paint)
        y += 40

        // --- Totals ---
        paint.typeface = Typeface.create(paint.typeface, Typeface.BOLD)

        // Total
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Total (စုစုပေါင်း):", 10f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${format(sale.sale.totalAmount)} Ks", rightX, y, paint)
        y += 35

        // Received
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Received (အမ်းငွေ):", 10f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${format(sale.sale.receivedAmount)} Ks", rightX, y, paint)
        y += 35

        // Change
        val change = sale.sale.receivedAmount - sale.sale.totalAmount
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Change (ပြန်အမ်းငွေ):", 10f, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${format(change)} Ks", rightX, y, paint)

        // --- Footer Message ---
        y += 60
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ကျေးဇူးအထူးတင်ပါသည်", centerX, y, paint)

        return bitmap
    }

    private fun format(amount: Int): String {
        return "%,d".format(amount)
    }
}