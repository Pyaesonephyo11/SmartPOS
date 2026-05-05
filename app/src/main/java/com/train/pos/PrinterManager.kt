package com.train.pos

import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import com.train.pos.printer.MyanmarTextBitmapHelper
import com.train.pos.printer.PaperWidth
import java.io.OutputStream


object PrinterManager {

    private var socket: BluetoothSocket? = null
    private var deviceName: String? = null
    private var outputStream: OutputStream? = null

    fun setSocket(btSocket: BluetoothSocket, name: String) {
        socket = btSocket
        deviceName = name
    }

    fun isConnected(): Boolean {
        return socket != null && socket!!.isConnected
    }

    fun getDeviceName(): String {
        return deviceName ?: "Unknown"
    }

    fun printText(text: String) {
        if (!isConnected()) return
        socket!!.outputStream.write(text.toByteArray())
        socket!!.outputStream.flush()
    }

    fun close() {
        socket?.close()
        socket = null
        deviceName = null
    }
    fun printBitmap(bitmap: Bitmap) {
        val bytes = EscPosBitmapHelper.bitmapToEscPos(bitmap)
        socket?.outputStream?.apply {
            write(bytes)
            flush()
        }
    }

    fun feedLine(lines: Int = 3) {
        printText("\n".repeat(lines))
    }
    private fun write(bytes: ByteArray) {
        socket?.outputStream?.write(bytes)
    }

    fun reset() = write(byteArrayOf(0x1B, 0x40))

    fun alignCenter() = write(byteArrayOf(0x1B, 0x61, 0x01))
    fun alignLeft() = write(byteArrayOf(0x1B, 0x61, 0x00))
    fun alignRight() = write(byteArrayOf(0x1B, 0x61, 0x02))

    fun boldOn() = write(byteArrayOf(0x1B, 0x45, 0x01))
    fun boldOff() = write(byteArrayOf(0x1B, 0x45, 0x00))

    fun line(text: String = "") {
        write((text + "\n").toByteArray())
    }

    fun divider() {
        line("--------------------------------")
    }

    fun feed(lines: Int = 1) {
        repeat(lines) { line() }
    }
    private var paperWidth: PaperWidth = PaperWidth.MM80

    fun setPaperWidth(width: PaperWidth) {
        paperWidth = width
    }

    private fun maxChars(): Int = paperWidth.chars

    fun lineLeftRight(
        left: String,
        right: String,
        bold: Boolean = false
    ) {
        if (!isConnected()) return

        val max = maxChars()

        val leftText = left.trim()
        val rightText = right.trim()

        val spaceCount = max - leftText.length - rightText.length

        val line = if (spaceCount > 0) {
            leftText + " ".repeat(spaceCount) + rightText
        } else {
            // overflow → wrap
            leftText + "\n" + rightText
        }

        if (bold) boldOn() else boldOff()
        write((line + "\n").toByteArray())
        boldOff()
    }
    fun itemLine(
        name: String,
        qty: Int,
        price: Int
    ) {
        val total = qty * price

        line(name) // item name (left)
        lineLeftRight(
            "$qty x ${"%,d".format(price)}",
            "%,d".format(total)
        )
    }
    fun printMyanmarText(context: Context, text: String) {
        if (!isConnected()) return

        val bitmap = MyanmarTextBitmapHelper.textToBitmap(
            context = context,
            text = text
        )
        printBitmap(bitmap)
    }


}


