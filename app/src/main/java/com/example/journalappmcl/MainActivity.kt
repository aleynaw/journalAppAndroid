package com.example.journalappmcl

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.journalappmcl.notification.NotificationManager
import com.example.journalappmcl.ui.JournalScreen
import com.example.journalappmcl.ui.UserIdScreen
import com.example.journalappmcl.viewmodel.JournalViewModel
import android.Manifest


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: JournalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = JournalViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
        // Initialize notifications
        NotificationManager(this).scheduleNotifications()

        // Check login first
        if (!isLoggedIn()) {
            println("🚪 Not logged in – redirecting to login")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContent {
            val context = LocalContext.current
            val userPreferences = remember { UserPreferences(context) }
            var userIdSet by remember { mutableStateOf(userPreferences.userId != null) }

            // Track whether we need to reset the UI
            val needsReset = remember { mutableStateOf(false) }

            // Listen for activity resume events
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        // Check if we should reset on resume
                        if (viewModel.isCompleted.value) {
                            needsReset.value = true
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Handle the reset when needed
            // Handle the reset and notification intent when needed
            LaunchedEffect(needsReset.value, intent) { // Add 'intent' as a key
                println("RELOAD or NEW INTENT") // More descriptive log

                // Handle the reset logic
                if (needsReset.value) {
                    viewModel.resetState()
                    needsReset.value = false
                }

                // Handle the notification intent logic
                // Check if the intent contains the identifier for your notification
                // We use 'currentIntent' to get the latest intent if onNewIntent was called
                val currentIntent = this@MainActivity.intent // Get the current intent of the activity

                if (currentIntent.hasExtra("notification_type")) {
                    val notificationType = currentIntent.getStringExtra("notification_type")

                    when (notificationType) {
                        "new_questions" -> {
                            // Trigger the feature to load new questions using your ViewModel
                            println("LOADED FROM NOTIFICATION: Triggering new questions.")
                            viewModel.loadNewQuestions() // Call your ViewModel function
                            // Consider removing the extra after handling to avoid re-triggering
                            currentIntent.removeExtra("notification_type")
                            currentIntent.removeExtra("notification_id")
                        }
                        // Handle other notification types if you have them
                        // "reminder" -> { ... }
                    }

                    // You can also get other extra data if needed
                    val notificationId = currentIntent.getIntExtra("notification_id", -1)
                    if (notificationId != -1) {
                        // Use the notification ID
                    }
                }
            }

            if (!userIdSet) {
                UserIdScreen(
                    userPreferences = userPreferences,
                    onUserIdSet = { userIdSet = true }
                )
            } else {
                JournalScreen(vm = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // No need to handle reset here, it's done in the Compose UI
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("isLoggedIn", false)
        println("🔐 isLoggedIn: $loggedIn")
        return loggedIn
    }
}