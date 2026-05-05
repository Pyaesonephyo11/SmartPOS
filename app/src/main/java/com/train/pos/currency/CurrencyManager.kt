package com.train.pos.currency


import android.content.Context

object CurrencyManager {

    private const val PREF = "currency_pref"
    private const val KEY = "currency"

    fun setCurrency(context: Context, currency: CurrencyType) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, currency.name)
            .apply()
    }

    fun getCurrency(context: Context): CurrencyType {
        val name = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, CurrencyType.MMK.name)

        return CurrencyType.valueOf(name!!)
    }
}
