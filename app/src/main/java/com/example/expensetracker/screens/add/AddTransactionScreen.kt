package com.example.expensetracker.screens.add

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.viewmodel.CategoryViewModel
import com.example.expensetracker.viewmodel.TransactionViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch

fun getCategoryIcon(category: String): String {
    return when (category.lowercase()) {
        "продукты" -> "🍔"
        "транспорт" -> "🚗"
        "зарплата" -> "💰"
        "развлечения" -> "🎉"
        "цветы" -> "🌸"
        "одежда" -> "🛍"
        "дом" -> "🏠"
        "здоровье" -> "💊"
        else -> "🏷"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    categoryViewModel: CategoryViewModel,
    transactionViewModel: TransactionViewModel
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    var type by remember { mutableStateOf("expense") }

    val categories by categoryViewModel.categories.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Добавить транзакцию", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = amount,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' }) {
                        amount = it
                        amountError = false
                    }
                },
                label = { Text("Сумма") },
                isError = amountError,
                leadingIcon = { Text("₽") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusRequester(focusRequester)
            )


            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(text = "${getCategoryIcon(category.name)} ${category.name}")
                            },
                            onClick = {
                                selectedCategory = category.name
                                expanded = false
                            }
                        )
                    }

                }
            }


            Row {
                Text("Тип транзакции:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = { type = "expense" },
                        label = { Text("Расход") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (type == "expense") MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            )
                            else MaterialTheme.colorScheme.surface
                        )
                    )
                    AssistChip(
                        onClick = { type = "income" },
                        label = { Text("Доход") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (type == "income") MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            )
                            else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Комментарий") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (amount.isBlank()) {
                        amountError = true
                        focusRequester.requestFocus()
                        scope.launch {
                            snackbarHostState.showSnackbar("Введите сумму")
                        }
                        return@Button
                    }

                    if (selectedCategory.isBlank()) {
                        categoryError = true
                        scope.launch {
                            snackbarHostState.showSnackbar("Выберите категорию")
                        }
                        return@Button
                    }

                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null) {
                        transactionViewModel.addTransaction(
                            amount = parsedAmount,
                            category = selectedCategory,
                            comment = comment,
                            type = type
                        )

                        amount = ""
                        selectedCategory = ""
                        comment = ""

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Транзакция добавлена",
                                actionLabel = "Отменить"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                transactionViewModel.undoLastTransaction()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Транзакция удалена")
                                }
                            }

                        }

                        amount = ""
                        selectedCategory = ""
                        comment = ""
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Введите корректную сумму")
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Сохранить",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сохранить")
            }

        }
    }
}