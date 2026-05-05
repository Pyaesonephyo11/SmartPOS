package com.train.pos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.train.pos.dao.CategoryDao
import com.train.pos.dao.ProductDao
import com.train.pos.dao.SaleDao
import com.train.pos.entries.CategoryEntity
import com.train.pos.entries.ProductEntity
import com.train.pos.entries.SaleEntity
import com.train.pos.entries.SaleItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(entities = [SaleEntity::class,
    SaleItemEntity::class,CategoryEntity::class,ProductEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao//version 3

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "burma_pos_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
