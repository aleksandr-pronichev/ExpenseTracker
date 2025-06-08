package com.example.expensetracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Transaction

@Database(
    entities = [Category::class, Transaction::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}