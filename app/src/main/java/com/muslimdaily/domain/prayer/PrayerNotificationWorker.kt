package com.muslimdaily.domain.prayer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.muslimdaily.R
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class PrayerNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val prayer = inputData.getString(KEY_PRAYER) ?: return Result.success()
        val time = inputData.getString(KEY_TIME) ?: "soon"
        createChannel(applicationContext)
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_splash)
            .setContentTitle("$prayer prayer is near")
            .setContentText("$prayer begins at $time. May Allah accept your prayer.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(prayer.hashCode(), notification)
        return Result.success()
    }

    companion object {
        private const val CHANNEL_ID = "prayer_reminders"
        private const val KEY_PRAYER = "prayer"
        private const val KEY_TIME = "time"

        fun schedule(context: Context, prayerName: String, triggerAt: LocalDateTime, displayTime: String) {
            val delay = Duration.between(LocalDateTime.now(), triggerAt).toMillis()
            if (delay <= 0) return
            val data = Data.Builder()
                .putString(KEY_PRAYER, prayerName)
                .putString(KEY_TIME, displayTime)
                .build()
            val request = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("prayer_reminder")
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_prayer),
                    NotificationManager.IMPORTANCE_HIGH
                )
                manager.createNotificationChannel(channel)
            }
        }
    }
}
