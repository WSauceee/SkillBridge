package com.example.skillbridge.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PostJobScreen(
    onBackClick: () -> Unit,
    onPostJob: (
        companyName: String,
        title: String,
        description: String,
        location: String,
        salary: String,
        jobType: String,
        skills: String
    ) -> Unit
) {

    var companyName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Post a Job",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Job Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = salary,
            onValueChange = { salary = it },
            label = { Text("Salary") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = jobType,
            onValueChange = { jobType = it },
            label = { Text("Job Type") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text("Required Skills") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                if (
                    companyName.isNotBlank() &&
                    title.isNotBlank() &&
                    description.isNotBlank()
                ) {

                    onPostJob(
                        companyName,
                        title,
                        description,
                        location,
                        salary,
                        jobType,
                        skills
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Post Job")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}