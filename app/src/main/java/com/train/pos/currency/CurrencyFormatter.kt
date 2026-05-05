package com.train.pos.currency


import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Int, currency: CurrencyType): String {
        return when (currency) {
            CurrencyType.MMK -> "%,d ${currency.symbol}".format(amount)
            CurrencyType.JPY -> "${currency.symbol}%,d".format(amount)
            CurrencyType.USD -> {
                val nf = NumberFormat.getNumberInstance(Locale.US)
                "${currency.symbol}${nf.format(amount)}.00"
            }
        }
    }
}
