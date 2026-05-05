package com.train.pos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoiceUtil {

    fun generate(): String {
        val date = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
            .format(Date())
        return "I-$date"
    }

}
