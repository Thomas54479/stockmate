package stockmate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import stockmate.data.Item
import stockmate.viewmodel.StockViewModel

// id == 0 means "add new item", otherwise we edit the item with that id
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(vm: StockViewModel, id: Long, onDone: () -> Unit) {
    // rememberSaveable keeps what the user typed when the phone rotates
    var name by rememberSaveable { mutableStateOf("") }
    var qty by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var limit by rememberSaveable { mutableStateOf("5") }
    var nameErr by rememberSaveable { mutableStateOf<String?>(null) }
    var qtyErr by rememberSaveable { mutableStateOf<String?>(null) }
    var priceErr by rememberSaveable { mutableStateOf<String?>(null) }
    var limitErr by rememberSaveable { mutableStateOf<String?>(null) }
    var loaded by rememberSaveable { mutableStateOf(id == 0L) }

    // Load the existing item once (not again after rotation, so edits aren't overwritten)
    LaunchedEffect(id) {
        if (!loaded) {
            vm.loadItem(id)?.let {
                name = it.name
                qty = it.quantity.toString()
                price = it.price.toString()
                limit = it.lowStockLimit.toString()
            }
            loaded = true
        }
    }

    fun save() {
        val q = qty.toIntOrNull()
        val p = price.toDoubleOrNull()
        val l = limit.toIntOrNull()
        nameErr = if (name.isBlank()) "Enter the item name" else null
        qtyErr = if (q == null || q < 0) "Enter a whole number, 0 or more" else null
        priceErr = if (p == null || p <= 0.0) "Enter a price greater than 0" else null
        limitErr = if (l == null || l < 0) "Enter a whole number, 0 or more" else null

        if (nameErr == null && qtyErr == null && priceErr == null && limitErr == null) {
            vm.saveItem(Item(id = id, name = name.trim(), quantity = q!!, price = p!!, lowStockLimit = l!!))
            onDone()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == 0L) "Add item" else "Edit item") },
                navigationIcon = { TextButton(onClick = onDone) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FormField("Item name", name, nameErr, KeyboardType.Text) { name = it }
            FormField("Quantity in stock", qty, qtyErr, KeyboardType.Number) { qty = it }
            FormField("Price per unit (K)", price, priceErr, KeyboardType.Decimal) { price = it }
            FormField("Warn me when stock is at or below", limit, limitErr, KeyboardType.Number) { limit = it }
            Button(onClick = { save() }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        }
    }
}
