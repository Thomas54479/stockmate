package com.bse3340.stockmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bse3340.stockmate.data.AppDatabase
import com.bse3340.stockmate.data.Item
import com.bse3340.stockmate.data.Sale
import com.bse3340.stockmate.notifications.LowStockWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).stockDao()

    val items: StateFlow<List<Item>> = dao.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val weekSales: StateFlow<List<Sale>> = dao.getSalesSince(startOfDay(6))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todaySales: StateFlow<List<Sale>> = weekSales
        .map { list -> val start = startOfDay(0); list.filter { it.timestamp >= start } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Label + total for each of the last 7 days (oldest first), used by the chart
    val weeklyTotals: StateFlow<List<Pair<String, Double>>> = weekSales
        .map { list ->
            (6 downTo 0).map { daysAgo ->
                val start = startOfDay(daysAgo)
                val end = start + DAY_MS
                val label = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(start))
                label to list.filter { it.timestamp in start until end }.sumOf { it.total }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun loadItem(id: Long): Item? = dao.getItem(id)

    fun saveItem(item: Item) {
        viewModelScope.launch {
            if (item.id == 0L) dao.insertItem(item) else dao.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch { dao.deleteItem(item) }
    }

    fun recordSale(itemId: Long, qty: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = dao.recordSale(itemId, qty)
            if (ok) LowStockWorker.checkNow(getApplication())   // alert straight away if stock is now low
            onResult(ok)
        }
    }

    private fun startOfDay(daysAgo: Int): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -daysAgo)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private companion object {
        const val DAY_MS = 24 * 60 * 60 * 1000L
    }
}
