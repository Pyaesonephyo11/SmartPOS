package com.train.pos.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.train.pos.entries.CategoryEntity

@Dao
interface CategoryDao {

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<CategoryEntity>>
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}
