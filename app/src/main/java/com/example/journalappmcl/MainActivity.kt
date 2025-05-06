package com.example.journalappmcl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.journalappmcl.ui.JournalScreen
import com.example.journalappmcl.ui.LoginScreen
import com.example.journalappmcl.ui.theme.JournalAppMCLTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HEY")
        // Redirect to login if user isn't authenticated
        if (!isLoggedIn()) {
            println("NOT LOGGED IN")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Load Compose UI
        setContent {
            JournalAppMCLTheme {
                MaterialTheme {
                    JournalScreen() // Main app screen
                }
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getBoolean("isLoggedIn", false)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JournalAppMCLTheme {
        Greeting("Android")
    }
}