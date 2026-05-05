package com.train.pos

import android.content.Context
import com.train.pos.model.ShopInfo

object ShopPref {

    private const val PREF = "shop_pref"

    fun save(ctx: Context, info: ShopInfo) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString("name", info.shopName)
            .putString("address", info.address)
            .putString("phone", info.phone)
            .apply()
    }

    fun load(ctx: Context): ShopInfo {
        val p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return ShopInfo(
            shopName = p.getString("name", "MY SHOP") ?: "",
            address = p.getString("address", "") ?: "",
            phone = p.getString("phone", "") ?: ""
        )
    }
}
