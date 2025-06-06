package com.example.journalappmcl.notification

import android.content.Context
import android.util.Log
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
                .setInitialDelay(20, TimeUnit.MINUTES)
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
//        scheduleAfternoonNotification()
//        scheduleEveningNotification()
//        scheduleNightNotification()
        scheduleFixedTimeNotification("2pm_notification", 14, 0)
        scheduleFixedTimeNotification("5pm_notification", 17, 0)
        scheduleFixedTimeNotification("8pm_notification", 20, 0)
    }

    fun scheduleFixedTimeNotification(tag: String, hour: Int, minute: Int) {
        val delay = calculateInitialDelay(hour, minute)
        Log.d("Scheduler", "Delay = $delay ms (${delay / 1000 / 60} min)")

        val workRequest = OneTimeWorkRequestBuilder<JournalNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(tag)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            tag,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Log.d("Scheduler", "🔔 Scheduled $tag notification for $hour:$minute (delay = $delay ms)")
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            // If the target time has already passed today, schedule for tomorrow
            target.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }
}


//    private fun scheduleAfternoonNotification() {
//        val afternoonWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
//            24, TimeUnit.HOURS
//        )
//            .setInitialDelay(calculateInitialDelay(14, 0), TimeUnit.MILLISECONDS)
//            .addTag(AFTERNOON_WORK)
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            AFTERNOON_WORK,
//            ExistingPeriodicWorkPolicy.KEEP,
//            afternoonWorkRequest
//        )
//        println("Scheduling Afternoon notification") // Optional log for debugging
//    }

//    private fun scheduleEveningNotification() {
//        val eveningWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
//            2, TimeUnit.MINUTES
//        )
//            .setInitialDelay(calculateInitialDelay(17, 0), TimeUnit.MILLISECONDS)
//            .addTag(EVENING_WORK)
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            EVENING_WORK,
//            ExistingPeriodicWorkPolicy.KEEP,
//            eveningWorkRequest
//        )
//        println("Scheduling Evening notification") // Optional log for debugging
//    }

//    fun scheduleEveningNotification() {
//        val workRequest = OneTimeWorkRequestBuilder<JournalNotificationWorker>()
//            .setInitialDelay(15, TimeUnit.MINUTES)
//            .addTag("evening_reschedule")
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniqueWork(
//            "evening_reschedule",
//            ExistingWorkPolicy.REPLACE,
//            workRequest
//        )
//    }
//
//    private fun scheduleNightNotification() {
//        val nightWorkRequest = PeriodicWorkRequestBuilder<JournalNotificationWorker>(
//            24, TimeUnit.HOURS
//        )
//            .setInitialDelay(calculateInitialDelay(19, 8), TimeUnit.MILLISECONDS)
//            .addTag(NIGHT_WORK)
//            .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            NIGHT_WORK,
//            ExistingPeriodicWorkPolicy.KEEP,
//            nightWorkRequest
//        )
//        println("Scheduling Night notification") // Optional log for debugging
//    }