package com.train.pos.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.train.pos.database.AppDatabase
import com.train.pos.entries.CategoryEntity
import com.train.pos.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository
    val categories: LiveData<List<CategoryEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(dao)
        categories = repository.categories
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insert(CategoryEntity(name = name))
        }
    }
    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }
}
