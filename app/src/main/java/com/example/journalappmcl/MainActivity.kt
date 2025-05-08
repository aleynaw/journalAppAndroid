package com.example.journalappmcl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.journalappmcl.ui.JournalScreen
import com.example.journalappmcl.ui.UserIdScreen
import com.example.journalappmcl.viewmodel.JournalViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: JournalViewModel

    private fun initializeApp() {
        // 🔐 Check login
        if (!isLoggedIn()) {
            println("🚪 Not logged in – redirecting to login")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // ✅ Launch main screen
        setContent {
            val context = LocalContext.current
            val userPreferences = remember { UserPreferences(context) }
            
            var userIdSet by remember { mutableStateOf(userPreferences.userId != null) }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = JournalViewModel()
        initializeApp()
    }

    override fun onResume() {
        println("YOOO")
        println(viewModel.isCompleted.value)
        super.onResume()
        // Re-run the same initialization logic as onCreate

        if (viewModel.isCompleted.value) {
            viewModel = JournalViewModel()
            initializeApp()
        }
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("isLoggedIn", false)
        println("🔐 isLoggedIn: $loggedIn")
        return loggedIn
    }
}