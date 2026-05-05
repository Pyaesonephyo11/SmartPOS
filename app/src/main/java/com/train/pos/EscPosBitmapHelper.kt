package com.train.pos

import android.graphics.Bitmap
import android.graphics.Color

object EscPosBitmapHelper {

    fun bitmapToEscPos(bitmap: Bitmap): ByteArray {
        val resized =
            Bitmap.createScaledBitmap(bitmap, 384, bitmap.height * 384 / bitmap.width, false)

        val width = resized.width
        val height = resized.height
        val bytes = ArrayList<Byte>()

        bytes.addAll(listOf(0x1D, 0x76, 0x30, 0x00).map { it.toByte() })
        bytes.add((width / 8).toByte())
        bytes.add(0x00)
        bytes.add((height % 256).toByte())
        bytes.add((height / 256).toByte())

        for (y in 0 until height) {
            for (x in 0 until width step 8) {
                var b = 0
                for (i in 0..7) {
                    val px = resized.getPixel(x + i, y)
                    val gray =
                        (Color.red(px) + Color.green(px) + Color.blue(px)) / 3
                    if (gray < 128) {
                        b = b or (1 shl (7 - i))
                    }
                }
                bytes.add(b.toByte())
            }
        }
        return bytes.toByteArray()
    }
}
