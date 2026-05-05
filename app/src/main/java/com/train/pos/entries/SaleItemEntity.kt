package com.train.pos.entries

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["saleId"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("saleId")]
)
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val saleId: Int,
    val productName: String,
    val qty: Int,
    val price: Int,
    val productId:Int,
    val costPrice: Int,
    val createAt: Long
){
    val total: Int get() = qty * price
    val costtotal: Int get() = qty * costPrice
    val profit: Int get() = (price - costPrice) * qty
}

