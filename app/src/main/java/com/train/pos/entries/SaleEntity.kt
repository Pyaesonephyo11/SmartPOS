package com.train.pos.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val saleId: Int = 0,
    val invoiceNo: String,
    val dateTime: Long,          // System.currentTimeMillis()
    val totalAmount: Int,
    val status: String,           // Completed / Cancelled
    val receivedAmount: Int
)

