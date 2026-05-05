package com.train.pos.model

data class ProductWithCategory(
    val productId: Int,
    val productName: String,
    val price: Int,
    val categoryName: String,
    val imageUri: String?,
    val stock: Int,
    val costPrice: Int
)
