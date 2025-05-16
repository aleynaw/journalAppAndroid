package com.example.journalappmcl.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.journalappmcl.MainActivity
import com.example.journalappmcl.R

class JournalNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "journal_notification_channel"
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        showQuestionNotification(applicationContext)
        return Result.success()
    }

    private fun showQuestionNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel (required for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "question_channel_id", // Unique channel ID
                "Question Notifications", // Channel name
                NotificationManager.IMPORTANCE_DEFAULT // Importance level
            ).apply {
                description = "Notifications for new questions"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent that will be triggered when the user taps the notification
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extra data to identify the notification
            putExtra("notification_id", 1)
            putExtra("notification_type", "new_questions")
        }

        // Create a PendingIntent for the tap action
        // Use a unique request code for this pending intent (e.g., based on notification ID)
        val tapPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            1, // Unique request code
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, "question_channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your small icon
            .setContentTitle("Time to follow up!")
            .setContentText("Tap to load a new set of questions.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(tapPendingIntent) // Set the tap action
            .setAutoCancel(true) // Dismiss the notification when tapped

        // Show the notification (Requires POST_NOTIFICATIONS permission on Android 13+)
        // You should handle the permission request in your Activity when necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build()) // Use a unique notification ID
            } else {
                // Permission not granted. You would typically handle requesting this
                // permission from the user earlier in your app flow.
                println("POST_NOTIFICATIONS permission not granted.")
            }
        } else {
            // For Android versions below 13, permission is granted at install time
            notificationManager.notify(1, builder.build()) // Use a unique notification ID
        }
    }
}