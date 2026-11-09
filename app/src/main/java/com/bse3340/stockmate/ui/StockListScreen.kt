package com.bse3340.stockmate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bse3340.stockmate.viewmodel.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    vm: StockViewModel,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onSell: () -> Unit,
    onSummary: () -> Unit
) {
    val items by vm.items.collectAsStateWithLifecycle()
    var deleteId by rememberSaveable { mutableLongStateOf(-1L) }   // survives rotation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("StockMate") },
                actions = {
                    TextButton(onClick = onSell) { Text("Sell") }
                    TextButton(onClick = onSummary) { Text("Summary") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "Add item") }
        }
    ) { padding ->
        if (items.isEmpty()) {
            // Empty state
            Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No items yet.\nTap + to add your first item.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    val low = item.quantity <= item.lowStockLimit
                    Card(Modifier.fillMaxWidth().clickable { onEdit(item.id) }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Qty: ${item.quantity}   |   ${money(item.price)} each")
                                if (low) {
                                    Text("Low stock!", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                            }
                            IconButton(onClick = { onEdit(item.id) }) { Icon(Icons.Default.Edit, "Edit ${item.name}") }
                            IconButton(onClick = { deleteId = item.id }) { Icon(Icons.Default.Delete, "Delete ${item.name}") }
                        }
                    }
                }
            }
        }
    }

    items.find { it.id == deleteId }?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteId = -1L },
            title = { Text("Delete ${item.name}?") },
            text = { Text("This removes the item from your stock list.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteItem(item); deleteId = -1L }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { deleteId = -1L }) { Text("Cancel") } }
        )
    }
}
