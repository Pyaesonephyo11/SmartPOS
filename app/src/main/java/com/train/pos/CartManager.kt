package com.train.pos

import com.train.pos.entries.ProductEntity
import com.train.pos.model.CartItem

object CartManager {

    val cartItems = mutableListOf<CartItem>()
    var incprice:Int=0


    fun addProduct(name: String, price: Int, cost: Int) {
        val item = cartItems.find { it.name == name }

        if (item == null) {
            incprice =price
          //  cartItems.add(CartItem(name, price, 1))
            cartItems.add(CartItem(name, price, 1,1,cost)) //inc dec
        } else {

            item.qty++
            item.price=incprice*item.qty


        }
    }

    fun addProduct(name: String, price: Int,productId: Int , cost: Int ) {
        val item = cartItems.find { it.name == name }

        if (item == null) {
            incprice =price
            cartItems.add(CartItem(name, price, 1,productId,cost)) //inc dec
        } else {

            item.qty++
          //  item.price=incprice*item.qty


        }
    }
    fun addProductStock(product: ProductEntity) {
        val item = cartItems.find { it.productId == product.id }

        if (item == null) {
            incprice =product.price.toInt()
            cartItems.add(CartItem(productId = product.id, name = product.name,
                price = product.price.toInt(), qty = 1, costPrice = product.costPrice)
            )
        } else {
            item.qty++
           // item.price=incprice*item.qty
        }
    }



    fun getTotalQty(): Int {
        return cartItems.sumOf { it.qty }
    }
    fun getTotalAmount(): Int {
        return cartItems.sumOf { it.qty * it.price }
    }

    fun clear() {
        cartItems.clear()
    }


}