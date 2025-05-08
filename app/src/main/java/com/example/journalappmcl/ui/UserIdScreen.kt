package com.example.journalappmcl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.journalappmcl.UserPreferences

@Composable
fun UserIdScreen(
    userPreferences: UserPreferences,
    onUserIdSet: () -> Unit
) {
    var userId by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Journal App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = userId,
            onValueChange = { 
                userId = it
                error = null
            },
            label = { Text("Enter your User ID") },
            isError = error != null,
            supportingText = { error?.let { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val id = userId.text.trim()
                if (id.isBlank()) {
                    error = "User ID cannot be empty"
                } else {
                    userPreferences.userId = id
                    onUserIdSet()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
} 