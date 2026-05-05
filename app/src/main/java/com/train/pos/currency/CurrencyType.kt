package com.train.pos.currency

enum class CurrencyType(
    val code: String,
    val symbol: String,
    val locale: String
) {
    MMK("MMK", "Ks", "my"),
    JPY("JPY", "¥", "ja"),
    USD("USD", "$", "en")
}
