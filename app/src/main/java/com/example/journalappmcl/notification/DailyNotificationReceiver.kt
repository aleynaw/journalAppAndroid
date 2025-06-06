package com.example.journalappmcl.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.journalappmcl.R
import java.util.Calendar

class DailyNotificationReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 1001)
        val requestCode = intent.getIntExtra("request_code", 0)
        val hour = intent.getIntExtra("hour", 14)
        val minute = intent.getIntExtra("minute", 0)

        val (title, text) = when (requestCode) {
            1 -> "Afternoon Reminder" to "It's time to journal!"
            2 -> "Evening Reminder" to "It's time to journal!"
            3 -> "Night Reminder" to "It's time to journal!"
            else -> "Daily Reminder" to "It's time to journal!"
        }

        // Intent to open MainActivity when notification is tapped
        val activityIntent = Intent(context, com.example.journalappmcl.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", "daily_reminder")
            putExtra("notification_id", notificationId)
        }

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Use unique requestCode for each notificationId
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, "daily_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)

        // Log before rescheduling:
        android.util.Log.d(
            "DailyNotificationReceiver",
            "Rescheduling notification (requestCode=$requestCode, notificationId=$notificationId) for tomorrow at $hour:$minute"
        )

        // Reschedule for tomorrow using original hour/minute:
        DailyNotificationScheduler.scheduleDailyNotification(
            context,
            hour,
            minute,
            requestCode,
            notificationId
        )
    }
}
