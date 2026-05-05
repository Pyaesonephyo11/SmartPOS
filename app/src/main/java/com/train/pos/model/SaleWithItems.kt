package com.train.pos.model

import androidx.room.Embedded
import androidx.room.Relation
import com.train.pos.entries.SaleEntity
import com.train.pos.entries.SaleItemEntity

data class SaleWithItems(
    @Embedded val sale: SaleEntity,

    @Relation(
        parentColumn = "saleId",
        entityColumn = "saleId"
    )
    val items: List<SaleItemEntity>
)

