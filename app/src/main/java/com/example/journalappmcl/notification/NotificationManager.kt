package com.example.journalappmcl.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationManager(private val context: Context) {

    companion object {
        private const val AFTERNOON_WORK = "afternoon_notification"
        private const val EVENING_WORK = "evening_notification"
        private const val NIGHT_WORK = "night_notification"

        fun schedule20minNotification() {
            val workRequest = OneTimeWorkRequestBuilder<JournalNotificationWorker>()
                    .setInitialDelay(20, TimeUnit.MINUTES)
                    .build()
        }
    }

    fun scheduleNotifications() {
        scheduleAfternoonNotification()
        scheduleEveningNotification()
        scheduleNightNotification()
    }

    private fun scheduleAfternoonNotification() {
        val afternoonWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(14, 0), TimeUnit.MILLISECONDS)
            .addTag(AFTERNOON_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            AFTERNOON_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            afternoonWorkRequest
        )
    }

    private fun scheduleEveningNotification() {
        val eveningWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(17, 0), TimeUnit.MILLISECONDS)
            .addTag(EVENING_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            EVENING_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            eveningWorkRequest
        )
    }

    private fun scheduleNightNotification() {
        val nightWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(20, 0), TimeUnit.MILLISECONDS)
            .addTag(NIGHT_WORK)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NIGHT_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            nightWorkRequest
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