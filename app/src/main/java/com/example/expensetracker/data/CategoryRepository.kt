package com.example.expensetracker.data

import com.example.expensetracker.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CategoryRepository(private val dao: CategoryDao) {

    val allCategories: Flow<List<Category>> = dao.getAllCategories()

    suspend fun insert(category: Category) {
        dao.insertCategory(category)
    }

    suspend fun clearAll() {
        dao.deleteUserCategories()
    }

    suspend fun delete(category: Category) {
        dao.deleteCategory(category)
    }

    suspend fun addDefaultCategories() {
        val existingCategories = dao.getAllCategories().first()

        if (existingCategories.isEmpty()) {
            dao.insertCategory(Category(name = "Продукты", isDefault = true))
            dao.insertCategory(Category(name = "Транспорт", isDefault = true))
            dao.insertCategory(Category(name = "Зарплата", isDefault = true))
            dao.insertCategory(Category(name = "Развлечения", isDefault = true))
        }
    }
}
