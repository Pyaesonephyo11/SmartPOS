package com.train.pos.model

import androidx.room.ColumnInfo

data class ProductSpinnerItem(
    val id: Int,
    val name: String,
    val price: Int,
    @ColumnInfo(name = "barcode")
    val barcodeData: String
)
