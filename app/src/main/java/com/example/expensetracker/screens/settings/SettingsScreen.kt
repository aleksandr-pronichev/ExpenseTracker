package com.example.expensetracker.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.viewmodel.CategoryViewModel
import com.example.expensetracker.viewmodel.TransactionViewModel

@Composable
fun SettingsScreen(
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Настройки", style = MaterialTheme.typography.headlineSmall)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showResetConfirm = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Сбросить все данные", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAboutDialog = true },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("О приложении", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("О приложении") },
            text = {
                Text("Приложение для учета финансов")
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    transactionViewModel.deleteAllTransactions()
                    categoryViewModel.deleteAllCategories()
                    showResetConfirm = false
                }) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Отмена")
                }
            },
            title = { Text("Подтверждение") },
            text = {
                Text("Вы уверены, что хотите удалить все данные?")
            }
        )
    }
}
