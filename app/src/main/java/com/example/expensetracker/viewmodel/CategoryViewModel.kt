package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.CategoryRepository
import com.example.expensetracker.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.addDefaultCategories()
        }
    }

    fun addCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insert(Category(name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }

    fun deleteAllCategories() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}