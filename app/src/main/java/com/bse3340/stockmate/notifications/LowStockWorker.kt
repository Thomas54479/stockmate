package com.bse3340.stockmate.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bse3340.stockmate.data.AppDatabase
import com.bse3340.stockmate.data.Item
import java.util.concurrent.TimeUnit

// Research topic 1: WorkManager + notifications.
// Runs once a day in the background (and right after each sale) and
// notifies the trader about items at or below their low-stock limit.
class LowStockWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val low = AppDatabase.get(applicationContext).stockDao().getLowStockItems()
        if (low.isNotEmpty()) showNotification(low)
        return Result.success()
    }

    private fun showNotification(low: List<Item>) {
        val ctx = applicationContext
        // Missing permission must not crash the app
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val manager = ctx.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Low stock alerts", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val text = low.joinToString(", ") { "${it.name} (${it.quantity} left)" }
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Low stock")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)
    }

    companion object {
        private const val CHANNEL_ID = "low_stock"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<LowStockWorker>(1, TimeUnit.DAYS).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("low_stock_daily", ExistingPeriodicWorkPolicy.KEEP, request)
        }

        fun checkNow(context: Context) {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<LowStockWorker>().build())
        }
    }
}
