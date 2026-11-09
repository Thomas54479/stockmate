package com.bse3340.stockmate

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bse3340.stockmate.notifications.LowStockWorker
import com.bse3340.stockmate.ui.AddEditItemScreen
import com.bse3340.stockmate.ui.RecordSaleScreen
import com.bse3340.stockmate.ui.StockListScreen
import com.bse3340.stockmate.ui.SummaryScreen
import com.bse3340.stockmate.ui.theme.StockmateTheme
import com.bse3340.stockmate.viewmodel.StockViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LowStockWorker.schedule(this)          // research topic 1: background work
        setContent {
            StockmateTheme { StockMateApp() }
        }
    }
}

@Composable
fun StockMateApp(vm: StockViewModel = viewModel()) {
    val nav = rememberNavController()
    AskNotificationPermission()

    NavHost(navController = nav, startDestination = "list") {
        composable("list") {
            StockListScreen(
                vm = vm,
                onAdd = { nav.navigate("edit/0") },
                onEdit = { id -> nav.navigate("edit/$id") },
                onSell = { nav.navigate("sell") },
                onSummary = { nav.navigate("summary") }
            )
        }
        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            AddEditItemScreen(vm, entry.arguments?.getLong("id") ?: 0L, onDone = { nav.popBackStack() })
        }
        composable("sell") { RecordSaleScreen(vm, onBack = { nav.popBackStack() }) }
        composable("summary") { SummaryScreen(vm, onBack = { nav.popBackStack() }) }
    }
}

// Android 13+ needs the user's permission to show notifications.
// If they refuse, the app keeps working; it just won't show the alerts.
@Composable
private fun AskNotificationPermission() {
    if (Build.VERSION.SDK_INT < 33) return
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
}
