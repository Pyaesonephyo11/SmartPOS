package com.train.pos.repository

import androidx.lifecycle.LiveData
import com.train.pos.dao.CategoryDao
import com.train.pos.entries.CategoryEntity

class CategoryRepository(private val dao: CategoryDao) {

    val categories: LiveData<List<CategoryEntity>> = dao.getAllCategories()

    suspend fun insert(category: CategoryEntity) {
        dao.insertCategory(category)
    }
    suspend fun delete(category: CategoryEntity) {
        dao.deleteCategory(category)
    }
}
