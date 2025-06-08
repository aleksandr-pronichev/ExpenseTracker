package com.example.expensetracker.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Главная")
    object Statistics : Screen("statistics", "Статистика")
    object AddTransaction : Screen("add_transaction", "Добавить")
    object Categories : Screen("categories", "Категории")
    object Settings : Screen("settings", "Настройки")
}