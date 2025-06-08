package com.example.expensetracker.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val scrollState = rememberScrollState()
    var selectedType by remember { mutableStateOf("Расходы") }

    val months = getLast12Months()
    var selectedMonth by remember { mutableStateOf(months.first()) }
    var expanded by remember { mutableStateOf(false) }

    val filteredExpenses = transactions.filter {
        it.amount < 0 && formatMonth(it.date) == selectedMonth
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedMonth)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            selectedMonth = month
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val incomes = transactions.filter {
            it.amount > 0 && formatMonth(it.date) == selectedMonth
        }
        val expenses = transactions.filter {
            it.amount < 0 && formatMonth(it.date) == selectedMonth
        }

        val incomeTotal = incomes.sumOf { it.amount }
        val expenseTotal = expenses.sumOf { it.amount }.absoluteValue

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD0F0C0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Доходы", style = MaterialTheme.typography.bodyMedium)
                    Text("+${incomeTotal.toInt()} ₽", color = Color(0xFF4CAF50))
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Расходы", style = MaterialTheme.typography.bodyMedium)
                    Text("-${expenseTotal.toInt()} ₽", color = Color(0xFFF44336))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedType = "Расходы" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "Расходы") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("Расходы")
            }

            Button(
                onClick = { selectedType = "Доходы" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "Доходы") MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("Доходы")
            }
        }


        Text(text = "Статистика", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        PieChartWithLegend(
            transactions = transactions.filter {
                if (selectedType == "Расходы") it.amount < 0 else it.amount > 0
            }.filter { formatMonth(it.date) == selectedMonth },
            type = selectedType
        )



        Spacer(modifier = Modifier.height(16.dp))

        Text("Последние транзакции", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val filteredByType = transactions.filter {
            if (selectedType == "Расходы") it.amount < 0 else it.amount > 0
        }.filter {
            formatMonth(it.date) == selectedMonth
        }

        filteredByType.forEach { transaction ->
            TransactionItem(transaction)
        }


        if (filteredByType.isEmpty()) {
            Text("Нет транзакций для отображения", modifier = Modifier.padding(top = 8.dp))
        }

    }
}

@Composable
fun PieChartWithLegend(transactions: List<Transaction>, type: String) {
    val categoryTotals = transactions
        .groupBy { it.category }
        .mapValues { it.value.sumOf { it.amount } }
        .filter { it.value != 0.0 }

    val total = categoryTotals.values.sumOf { it.absoluteValue }

    if (categoryTotals.isEmpty() || total == 0.0) {
        Text("Нет данных для отображения диаграммы", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val entries = categoryTotals.map { (category, value) ->
        PieEntry(value.absoluteValue.toFloat(), category)
    }

    val colors = listOf(
        Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFA726),
        Color(0xFFAB47BC), Color(0xFFEF5350), Color(0xFF26C6DA)
    ).take(entries.size).map { it.toArgb() }

    val dataSet = PieDataSet(entries, null).apply {
        this.colors = colors
        setDrawValues(false) //
        valueTextSize = 30f
        valueTextColor = Color.White.toArgb()
        sliceSpace = 2f
    }

    val pieData = PieData(dataSet)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AndroidView(
                factory = { context ->
                    PieChart(context).apply {
                        setUsePercentValues(false)
                        setDrawHoleEnabled(true)
                        setDrawEntryLabels(false)
                        holeRadius = 60f
                        transparentCircleRadius = 65f
                        description.isEnabled = false
                        legend.isEnabled = false
                    }
                },
                update = { chart ->
                    chart.data = pieData
                    chart.centerText = "$type\n${total.toInt()} ₽"
                    chart.setCenterTextSize(16f)
                    chart.animateY(800)
                    chart.invalidate()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                categoryTotals.entries.forEachIndexed { index, (category, value) ->
                    val percent = (value.absoluteValue / total * 100).roundToInt()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(colors[index % colors.size]), shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$category: $percent%", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = transaction.category)
                Text(text = formatDate(transaction.date), style = MaterialTheme.typography.bodySmall)
                if (transaction.comment.isNotBlank()) {
                    Text(text = transaction.comment, style = MaterialTheme.typography.bodySmall)
                }
            }
            val isIncome = transaction.amount > 0

            Text(
                text = (if (isIncome) "+" else "") + "${transaction.amount.toInt()} ₽",
                color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336),
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getLast12Months(): List<String> {
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    return List(12) {
        val month = formatter.format(calendar.time)
        calendar.add(Calendar.MONTH, -1)
        month.replaceFirstChar { it.uppercase() }
    }
}

fun formatMonth(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
}

