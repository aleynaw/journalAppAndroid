package com.example.journalappmcl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.journalappmcl.ui.JournalScreen
import com.example.journalappmcl.ui.UserIdScreen
import com.example.journalappmcl.viewmodel.JournalViewModel


class MainActivity : ComponentActivity() {
    private lateinit var viewModel: JournalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = JournalViewModel()

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
            LaunchedEffect(needsReset.value) {
                if (needsReset.value) {
                    viewModel.resetState()
                    needsReset.value = false
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