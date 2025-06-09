package com.example.expensetracker.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.expensetracker.model.Category
import com.example.expensetracker.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var recentlyDeletedCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            Text("Категории", style = MaterialTheme.typography.headlineSmall)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Новая категория") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val name = textFieldValue.text.trim()
                    if (name.isNotBlank()) {
                        viewModel.addCategory(name)
                        textFieldValue = TextFieldValue()
                    }
                }) {
                    Text("Добавить")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            val defaultCategories = listOf("Зарплата", "Продукты", "Развлечения", "Транспорт", "Здоровье", "Дом", "Одежда")

            val sortedCategories = buildList {
                addAll(categories.filter { it.name in defaultCategories })
                addAll(categories.filter { it.name !in defaultCategories })
            }

            LazyColumn {
                items(sortedCategories) { category ->
                    val isDefault = category.name in listOf("Зарплата", "Продукты", "Развлечения", "Транспорт")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${getCategoryIcon(category.name)} ${category.name}")
                        if (!isDefault) {
                            IconButton(onClick = {
                                viewModel.deleteCategory(category)
                                recentlyDeletedCategory = category
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Категория удалена",
                                        actionLabel = "Отменить"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        recentlyDeletedCategory?.let {
                                            viewModel.addCategory(it.name)
                                            recentlyDeletedCategory = null
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить категорию"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
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
        else -> "🏷"
    }
}
