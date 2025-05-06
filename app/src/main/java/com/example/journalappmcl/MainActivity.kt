package com.example.journalappmcl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.journalappmcl.ui.JournalScreen
import com.example.journalappmcl.ui.theme.JournalAppMCLTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            JournalAppMCLTheme {
                MaterialTheme {
                    JournalScreen()
                }
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val loggedIn = prefs.getBoolean("isLoggedIn", false)
        println("🔐 isLoggedIn: $loggedIn")
        return loggedIn
    }
}