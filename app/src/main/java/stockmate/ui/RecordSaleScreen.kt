package stockmate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import stockmate.viewmodel.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSaleScreen(vm: StockViewModel, onBack: () -> Unit) {
    val items by vm.items.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableLongStateOf(-1L) }
    var qty by rememberSaveable { mutableStateOf("") }
    var qtyErr by rememberSaveable { mutableStateOf<String?>(null) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    fun sell() {
        message = null
        qtyErr = null
        val q = qty.toIntOrNull()
        val item = items.find { it.id == selectedId }
        when {
            item == null -> message = "Choose an item first"
            q == null || q <= 0 -> qtyErr = "Enter a whole number greater than 0"
            q > item.quantity -> qtyErr = "Only ${item.quantity} in stock"
            else -> vm.recordSale(item.id, q) { ok ->
                message = if (ok) "Sale recorded: ${money(q * item.price)}" else "Sale failed, please try again"
                if (ok) qty = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record sale") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Text("Add an item to your stock first.", Modifier.padding(padding).padding(16.dp))
        } else {
            Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose item", style = MaterialTheme.typography.titleMedium)
                LazyColumn(Modifier.weight(1f)) {
                    items(items, key = { it.id }) { item ->
                        Row(
                            Modifier.fillMaxWidth().clickable { selectedId = item.id }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = selectedId == item.id, onClick = { selectedId = item.id })
                            Column {
                                Text(item.name)
                                Text("In stock: ${item.quantity}  |  ${money(item.price)} each",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                FormField("Quantity sold", qty, qtyErr, KeyboardType.Number) { qty = it }
                message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                Button(onClick = { sell() }, modifier = Modifier.fillMaxWidth()) { Text("Record sale") }
            }
        }
    }
}
