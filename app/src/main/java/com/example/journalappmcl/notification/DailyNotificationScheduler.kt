package com.example.journalappmcl.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object DailyNotificationScheduler {

    fun scheduleDailyNotification(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int, // different for each notification
        notificationId: Int // different for each notification
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyNotificationReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
            putExtra("request_code", requestCode)
            putExtra("hour", hour)    // Save hour
            putExtra("minute", minute) // Save minute
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // must be unique per alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // move to tomorrow if time has passed today
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                val intentSettings = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intentSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intentSettings)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        // Log for testing:
        android.util.Log.d(
            "DailyNotificationScheduler",
            "Scheduled notification (requestCode=$requestCode, notificationId=$notificationId) for $hour:$minute"
        )
    }
}
