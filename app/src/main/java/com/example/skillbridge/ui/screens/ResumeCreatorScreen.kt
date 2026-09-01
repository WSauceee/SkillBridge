package com.example.skillbridge.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillbridge.data.User
import com.example.skillbridge.data.decodeEducation
import com.example.skillbridge.data.decodeExperience
import com.example.skillbridge.data.decodeSkills
import com.example.skillbridge.util.ResumePdfGenerator
import kotlinx.coroutines.launch

private val AccentGreen = Color(0xFF3DBE6B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeCreatorScreen(user: User, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val education = user.educationRaw.decodeEducation()
    val skills = user.skillsRaw.decodeSkills()
    val experience = user.experienceRaw.decodeExperience()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Your Resume") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(user.fullName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                if (!user.extraInfo.isNullOrBlank()) {
                    Text(user.extraInfo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            ResumeSection(title = "Education") {
                if (education.isEmpty()) {
                    Text("No education added yet", color = Color.Gray)
                } else {
                    education.forEach {
                        Text(it.degree, fontWeight = FontWeight.Medium)
                        Text("${it.institution} • ${it.year}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            ResumeSection(title = "Skills") {
                if (skills.isEmpty()) {
                    Text("No skills added yet", color = Color.Gray)
                } else {
                    Text(skills.joinToString(", "))
                }
            }

            ResumeSection(title = "Experience") {
                if (experience.isEmpty()) {
                    Text("No experience added yet", color = Color.Gray)
                } else {
                    experience.forEach {
                        Text(it.jobTitle, fontWeight = FontWeight.Medium)
                        Text(it.companyAndDuration, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            Button(
                onClick = {
                    val file = ResumePdfGenerator.generate(context, user, education, skills, experience)
                    val uri = ResumePdfGenerator.getShareUri(context, file)
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Open Resume PDF"))
                    } catch (e: ActivityNotFoundException) {
                        scope.launch { snackbarHostState.showSnackbar("PDF saved, but no app found to open it") }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save as PDF")
            }
        }
    }
}

@Composable
private fun ResumeSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}