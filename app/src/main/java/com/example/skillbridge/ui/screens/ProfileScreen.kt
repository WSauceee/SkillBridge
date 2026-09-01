package com.example.skillbridge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillbridge.data.EducationEntry
import com.example.skillbridge.data.ExperienceEntry
import com.example.skillbridge.data.User
import com.example.skillbridge.data.decodeEducation
import com.example.skillbridge.data.decodeExperience
import com.example.skillbridge.data.decodeSkills

// Same palette as the dashboard (JobSeekerHomeScreen.kt) — duplicated locally since
// each screen file here is self-contained; feel free to move these into one shared
// file later if you'd rather not repeat them.
private val HeaderBlue = Color(0xFF3169F0)
private val AccentGreen = Color(0xFF3DBE6B)
private val ChipBg = Color(0xFFE8EFFE)

@Composable
fun ProfileScreen(
    user: User,
    onAddEducation: (EducationEntry) -> Unit,
    onAddSkill: (String) -> Unit,
    onAddExperience: (ExperienceEntry) -> Unit,
    onGenerateResumeClick: () -> Unit
) {
    val education = user.educationRaw.decodeEducation()
    val skills = user.skillsRaw.decodeSkills()
    val experience = user.experienceRaw.decodeExperience()

    val sectionsDone = listOf(true, education.isNotEmpty(), skills.isNotEmpty(), experience.isNotEmpty())
    val strength = (sectionsDone.count { it } * 100) / sectionsDone.size
    val nextMissing = when {
        education.isEmpty() -> "education"
        skills.isEmpty() -> "skills"
        experience.isEmpty() -> "experience"
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(HeaderBlue)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text("My Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text("Complete your profile to build your resume", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileStrengthCard(strength, nextMissing)
            PersonalInfoCard(user)
            EducationSectionCard(entries = education, onAdd = onAddEducation)
            SkillsSectionCard(entries = skills, onAdd = onAddSkill)
            ExperienceSectionCard(entries = experience, onAdd = onAddExperience)

            Button(
                onClick = onGenerateResumeClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Resume")
            }
        }
    }
}

@Composable
private fun ProfileStrengthCard(strengthPercent: Int, nextMissingSection: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Profile Strength", fontWeight = FontWeight.Bold)
                Text("$strengthPercent%", color = AccentGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = strengthPercent / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AccentGreen,
                trackColor = ChipBg
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (nextMissingSection != null) "Add $nextMissingSection to reach 100%" else "Your profile is complete!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ProfileCardShell(
    icon: ImageVector,
    title: String,
    completed: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = HeaderBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold)
                        Text(
                            if (completed) "✓ Completed" else "Add details",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (completed) AccentGreen else Color.Gray
                        )
                    }
                }
                if (completed) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(50))
                            .background(AccentGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = HeaderBlue)
                }
            }
            content()
        }
    }
}

@Composable
private fun EntryRow(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        if (!subtitle.isNullOrBlank()) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
private fun PersonalInfoCard(user: User) {
    ProfileCardShell(
        icon = Icons.Filled.Person,
        title = "Personal Information",
        completed = true,
        onHeaderClick = {}
    ) {
        Spacer(Modifier.height(8.dp))
        EntryRow(title = user.fullName, subtitle = user.email)
    }
}

@Composable
private fun EducationSectionCard(entries: List<EducationEntry>, onAdd: (EducationEntry) -> Unit) {
    var expanded by remember(entries.size) { mutableStateOf(entries.isEmpty()) }
    var degree by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    ProfileCardShell(
        icon = Icons.Filled.School,
        title = "Education",
        completed = entries.isNotEmpty(),
        onHeaderClick = { expanded = !expanded }
    ) {
        entries.forEach { EntryRow(title = it.degree, subtitle = "${it.institution} • ${it.year}") }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = institution, onValueChange = { institution = it }, label = { Text("Institution") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Year (e.g. 2021–2024)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (degree.isNotBlank() && institution.isNotBlank()) {
                        onAdd(EducationEntry(degree, institution, year))
                        degree = ""; institution = ""; year = ""
                        expanded = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}

@Composable
private fun SkillsSectionCard(entries: List<String>, onAdd: (String) -> Unit) {
    var expanded by remember(entries.size) { mutableStateOf(entries.isEmpty()) }
    var name by remember { mutableStateOf("") }

    ProfileCardShell(
        icon = Icons.Filled.Star,
        title = "Skills",
        completed = entries.isNotEmpty(),
        onHeaderClick = { expanded = !expanded }
    ) {
        if (entries.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                entries.forEach { skill ->
                    Surface(shape = RoundedCornerShape(50), color = ChipBg) {
                        Text(skill, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = HeaderBlue)
                    }
                }
            }
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Skill") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name)
                        name = ""
                        expanded = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}

@Composable
private fun ExperienceSectionCard(entries: List<ExperienceEntry>, onAdd: (ExperienceEntry) -> Unit) {
    var expanded by remember(entries.size) { mutableStateOf(entries.isEmpty()) }
    var jobTitle by remember { mutableStateOf("") }
    var companyAndDuration by remember { mutableStateOf("") }

    ProfileCardShell(
        icon = Icons.Filled.Work,
        title = "Experience",
        completed = entries.isNotEmpty(),
        onHeaderClick = { expanded = !expanded }
    ) {
        entries.forEach { EntryRow(title = it.jobTitle, subtitle = it.companyAndDuration) }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text("Job Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = companyAndDuration, onValueChange = { companyAndDuration = it }, label = { Text("Company & Duration") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (jobTitle.isNotBlank()) {
                        onAdd(ExperienceEntry(jobTitle, companyAndDuration))
                        jobTitle = ""; companyAndDuration = ""
                        expanded = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}