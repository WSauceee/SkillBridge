package com.example.skillbridge.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skillbridge.data.AccountType
import com.example.skillbridge.data.User

@Composable
fun HomeScreen(user: User) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome, ${user.fullName}!", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        AssistChip(
            onClick = {},
            label = {
                Text(if (user.accountType == AccountType.JOB_SEEKER) "Job Seeker Account" else "Business Account")
            }
        )
    }
}