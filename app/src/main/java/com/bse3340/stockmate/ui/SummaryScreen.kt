package com.bse3340.stockmate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bse3340.stockmate.viewmodel.StockViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(vm: StockViewModel, onBack: () -> Unit) {
    val today by vm.todaySales.collectAsStateWithLifecycle()
    val weekly by vm.weeklyTotals.collectAsStateWithLifecycle()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily summary") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Today's sales", style = MaterialTheme.typography.titleMedium)
                Text(money(today.sumOf { it.total }), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            }
            item {
                Text("Last 7 days", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                BarChart(weekly)
            }
            item { Text("Sales today", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp)) }
            if (today.isEmpty()) {
                item { Text("No sales recorded today yet.") }
            } else {
                items(today, key = { it.id }) { sale ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${timeFormat.format(Date(sale.timestamp))}  ${sale.itemName} x${sale.quantity}")
                        Text(money(sale.total))
                    }
                }
            }
        }
    }
}

// Research topic 2: a simple bar chart drawn with Compose (no library needed)
@Composable
fun BarChart(data: List<Pair<String, Double>>) {
    val max = (data.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    Row(
        Modifier.fillMaxWidth().height(170.dp).padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(if (value > 0) "%.0f".format(value) else "", fontSize = 10.sp)
                Box(
                    Modifier
                        .fillMaxWidth(0.6f)
                        .height((110f * (value / max).toFloat()).dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(label, fontSize = 11.sp)
            }
        }
    }
}
