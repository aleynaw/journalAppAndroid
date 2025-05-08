package com.example.journalappmcl.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationManager(private val context: Context) {

    companion object {
        private const val MORNING_WORK = "morning_notification"
        private const val EVENING_WORK = "evening_notification"
    }

    fun scheduleNotifications() {
        scheduleMorningNotification()
        scheduleEveningNotification()
    }

    private fun scheduleMorningNotification() {
        val morningWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(9, 0), TimeUnit.MILLISECONDS)
            .addTag(MORNING_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MORNING_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            morningWorkRequest
        )
    }

    private fun scheduleEveningNotification() {
        val eveningWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(18, 0), TimeUnit.MILLISECONDS)
            .addTag(EVENING_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            EVENING_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            eveningWorkRequest
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        var targetTime = calendar.timeInMillis
        if (targetTime <= currentTime) {
            targetTime += TimeUnit.DAYS.toMillis(1)
        }

        return targetTime - currentTime
    }
} 