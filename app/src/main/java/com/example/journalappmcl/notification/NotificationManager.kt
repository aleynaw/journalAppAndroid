package com.example.journalappmcl.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationManager(private val context: Context) {

    companion object {
        private const val AFTERNOON_WORK = "afternoon_notification"
        private const val EVENING_WORK = "evening_notification"
        private const val NIGHT_WORK = "night_notification"
        private const val YES_MIN_WORK = "20_min_notification"

        fun schedule20minNotification(context: Context) {
            println("Scheduling 20-min notification") // Optional log for debugging

            val workRequest = OneTimeWorkRequestBuilder<JournalNotificationWorker>()
                .setInitialDelay(1, TimeUnit.SECONDS)
                .addTag(YES_MIN_WORK) // Add a tag for easier identification if needed
                .build()

            // Get an instance of WorkManager and enqueue the work request
            androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
                YES_MIN_WORK, // Provide a unique name for this work
                androidx.work.ExistingWorkPolicy.REPLACE, // Define how to handle existing work with the same unique name
                workRequest
            )
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