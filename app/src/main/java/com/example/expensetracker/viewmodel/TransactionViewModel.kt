package com.example.expensetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.TransactionRepository
import com.example.expensetracker.model.Category
import com.example.expensetracker.model.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private var recentlyDeletedTransaction: Transaction? = null
    private var lastTransaction: Transaction? = null

    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(amount: Double, category: String, comment: String, type: String) {
        viewModelScope.launch {
            val transactionAmount = if (type == "expense") -amount else amount

            val transaction = Transaction(
                amount = transactionAmount,
                category = category,
                comment = comment,
                type = type
            )
            lastTransaction = repository.insert(transaction)
        }
    }

    fun undoLastTransaction() {
        viewModelScope.launch {
            lastTransaction?.let {
                repository.deleteTransaction(it)
                lastTransaction = null
            }
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            recentlyDeletedTransaction = transaction
        }
    }

    fun restoreLastTransaction() {
        viewModelScope.launch {
            recentlyDeletedTransaction?.let {
                repository.insert(it)
                recentlyDeletedTransaction = null
            }
        }
    }


}