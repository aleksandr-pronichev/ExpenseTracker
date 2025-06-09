package com.example.expensetracker.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.expensetracker.data.AppDatabase
import com.example.expensetracker.data.CategoryRepository
import com.example.expensetracker.data.TransactionRepository
import com.example.expensetracker.screens.add.AddTransactionScreen
import com.example.expensetracker.screens.categories.CategoryScreen
import com.example.expensetracker.screens.dashboard.DashboardScreen
import com.example.expensetracker.screens.settings.SettingsScreen
import com.example.expensetracker.screens.statistics.StatisticsScreen
import com.example.expensetracker.viewmodel.CategoryViewModel
import com.example.expensetracker.viewmodel.CategoryViewModelFactory
import com.example.expensetracker.viewmodel.TransactionViewModel
import com.example.expensetracker.viewmodel.TransactionViewModelFactory

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val db = rememberDatabase(context)

    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(CategoryRepository(db.categoryDao()))
    )

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(TransactionRepository(db.transactionDao()))
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = transactionViewModel) }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                categoryViewModel = categoryViewModel,
                transactionViewModel = transactionViewModel
            )
        }
        composable(Screen.Categories.route) {
            CategoryScreen(viewModel = categoryViewModel)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(viewModel = transactionViewModel) }
        composable(Screen.Settings.route) {
            SettingsScreen(
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel
            )
        }
    }
}

@Composable
fun rememberDatabase(context: Context): AppDatabase {
    return remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "expenses-db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
}
