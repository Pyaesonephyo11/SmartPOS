package com.train.pos.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products",
    )
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Int,
    val categoryId: Int,
    val imageUri: String?,
    val stock: Int,
    val barcode: String? = null,  //barcode
    val costPrice : Int
)

