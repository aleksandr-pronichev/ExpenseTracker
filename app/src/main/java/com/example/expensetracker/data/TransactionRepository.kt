package com.example.expensetracker.data

import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()

    suspend fun insert(transaction: Transaction): Transaction {
        val id = dao.insertTransaction(transaction)
        return transaction.copy(id = id.toInt())
    }

    suspend fun clearAll() {
        dao.clearAllTransactions()
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }
}
