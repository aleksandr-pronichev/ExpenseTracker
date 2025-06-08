package com.example.expensetracker.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.FlowRow
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    var selectedCategory by remember { mutableStateOf("") }
    val categories = listOf("Все", "Продукты", "Транспорт", "Зарплата", "Развлечения")

    val filteredTransactions = if (selectedCategory.isBlank() || selectedCategory == "Все") {
        transactions
    } else {
        transactions.filter { it.category == selectedCategory }
    }

    val totalBalance = transactions.sumOf { it.amount }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF7FF)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ваш баланс:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${totalBalance.toInt()} ₽",
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (totalBalance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    AssistChip(
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                "${getCategoryIcon(category)} $category",
                                color = if (selectedCategory == category)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            if (selectedCategory == category) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedCategory == category)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Text("Последние транзакции", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredTransactions) { transaction ->
                    TransactionItem(transaction = transaction, onDelete = { toDelete ->
                        viewModel.deleteTransaction(toDelete)

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Удалено: ${toDelete.category}",
                                actionLabel = "Отменить"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.restoreLastTransaction()
                            }
                        }

                    })
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: (Transaction) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.category)
                Text(formatDate(transaction.date), style = MaterialTheme.typography.bodySmall)
                if (transaction.comment.isNotBlank()) {
                    Text(
                        text = transaction.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = (if (transaction.amount > 0) "+" else "") + "${transaction.amount.toInt()} ₽",
                    color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { onDelete(transaction) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getCategoryIcon(category: String): String {
    return when (category.lowercase()) {
        "продукты" -> "🍔"
        "транспорт" -> "🚗"
        "зарплата" -> "💰"
        "развлечения" -> "🎉"
        "здоровье" -> "💊"
        "дом" -> "🏠"
        "одежда" -> "👕"
        else -> "📝"
    }
}
