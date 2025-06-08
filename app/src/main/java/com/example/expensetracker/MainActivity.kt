package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.navigation.NavGraph
import com.example.expensetracker.navigation.Screen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        val items = listOf(
                            Screen.Dashboard to Icons.Filled.Home,
                            Screen.Statistics to Icons.Filled.BarChart,
                            Screen.AddTransaction to Icons.Filled.AddCircle,
                            Screen.Categories to Icons.Filled.Category,
                            Screen.Settings to Icons.Filled.Settings
                        )

                        NavigationBar {
                            items.forEach { (screen, icon) ->
                                NavigationBarItem(
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    label = {
                                        Text(
                                            screen.title,
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall
                                            )
                                            },
                                    icon = { Icon(icon, contentDescription = screen.title) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    NavGraph(navController = navController, modifier = Modifier.padding(padding))
                }
            }
        }
    }
}