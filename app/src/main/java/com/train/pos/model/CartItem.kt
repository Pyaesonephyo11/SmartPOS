package com.train.pos.model

data class CartItem(
    val name: String,
    var price: Int,
    var qty: Int,
    val productId: Int ,// inc dec
    val costPrice: Int
){
    val subTotal: Int
        get() = price * qty
}
