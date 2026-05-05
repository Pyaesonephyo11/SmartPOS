package com.train.pos.model


data class MonthlyReportItem(
    val productId: Int,
    val productName: String,
    val price: Int,
    val costPrice: Int,
    val sellingQty: Int,
    val totalRevenue: Int,
    val totalProfit: Int
)

