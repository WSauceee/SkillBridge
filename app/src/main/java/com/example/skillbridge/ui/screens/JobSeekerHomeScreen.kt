package com.example.skillbridge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.skillbridge.data.Job
import com.example.skillbridge.data.JobApplication
import com.example.skillbridge.data.User
import kotlinx.coroutines.launch


private val HeaderBlue = Color(0xFF3169F0)
private val AccentGreen = Color(0xFF3DBE6B)
private val ChipBg = Color(0xFFE8EFFE)

private enum class BottomTab(val label: String) {
    HOME("Home"), JOBS("Jobs"), APPLICATIONS("Applications"), PROFILE("Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSeekerHomeScreen(
    user: User,
    jobs: List<Job>,
    applications: List<JobApplication>,
    onResumeCreatorClick: () -> Unit,
    onAddEducation: (EducationEntry) -> Unit,
    onAddSkill: (String) -> Unit,
    onAddExperience: (ExperienceEntry) -> Unit,
    onApply: (Job) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showComingSoon(feature: String) {
        scope.launch { snackbarHostState.showSnackbar("$feature — coming soon") }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
                    onClick = { selectedTab = BottomTab.HOME },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.JOBS,
                    onClick = { selectedTab = BottomTab.JOBS },
                    icon = { Icon(Icons.Filled.Work, contentDescription = "Jobs") },
                    label = { Text("Jobs") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.APPLICATIONS,
                    onClick = { selectedTab = BottomTab.APPLICATIONS },
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "Applications") },
                    label = { Text("Applications") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.PROFILE,
                    onClick = { selectedTab = BottomTab.PROFILE },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomTab.HOME -> HomeTabContent(
                    user = user,
                    jobs = jobs,
                    onPlaceholderClick = { showComingSoon(it) },
                    onLogout = onLogout,
                    onSeeAllJobs = { selectedTab = BottomTab.JOBS },
                    onApply = { 
                        onApply(it)
                        scope.launch { snackbarHostState.showSnackbar("Applied to ${it.title} successfully") }
                    }
                )
                BottomTab.PROFILE -> ProfileScreen(
                    user = user,
                    onAddEducation = onAddEducation,
                    onAddSkill = onAddSkill,
                    onAddExperience = onAddExperience,
                    onGenerateResumeClick = onResumeCreatorClick
                )
                BottomTab.APPLICATIONS -> SeekerApplicationsTabContent(
                    applications = applications,
                    jobs = jobs
                )
                BottomTab.JOBS -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "All Jobs",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (jobs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No jobs available yet", color = Color.Gray)
                        }
                    } else {
                        jobs.forEach { job ->
                            JobCard(
                                job = job,
                                onClick = { showComingSoon("Job Details") },
                                onApply = {
                                    onApply(job)
                                    scope.launch { snackbarHostState.showSnackbar("Applied to ${job.title} successfully") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    user: User,
    jobs: List<Job>,
    onPlaceholderClick: (String) -> Unit,
    onLogout: () -> Unit,
    onSeeAllJobs: () -> Unit,
    onApply: (Job) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroHeader(
            userName = user.fullName.substringBefore(" "),
            onBellClick = { onPlaceholderClick("Notifications") },
            onSearchClick = { onPlaceholderClick("Search") },
            onLogout = onLogout
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))

            StatCard(Icons.Filled.Work, "5", "Jobs Saved", Modifier.fillMaxWidth())

            Spacer(Modifier.height(24.dp))
            SectionHeader("Latest Job", onSeeAllJobs)
            Spacer(Modifier.height(12.dp))
            
            if (jobs.isEmpty()) {
                Text("No jobs available", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                JobCard(
                    job = jobs.first(),
                    onClick = { onPlaceholderClick("Job details") },
                    onApply = onApply
                )
            }

            Spacer(Modifier.height(20.dp))
            InternshipBanner(onClick = { onPlaceholderClick("Internships") })
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SeekerApplicationsTabContent(
    applications: List<JobApplication>,
    jobs: List<Job>
) {
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
            Text("My Applications", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text("Track the status of your job applications", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
        }

        Column(modifier = Modifier.padding(20.dp)) {
            if (applications.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("You haven't applied to any jobs yet", color = Color.Gray)
                }
            } else {
                applications.forEach { app ->
                    val job = jobs.find { it.id == app.jobId }
                    SeekerApplicationCard(application = app, jobTitle = job?.title ?: "Unknown Job", companyName = job?.companyName ?: "Unknown Company")
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SeekerApplicationCard(application: JobApplication, jobTitle: String, companyName: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(jobTitle, fontWeight = FontWeight.Bold)
                    Text(companyName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when(application.status) {
                        "Pending" -> Color(0xFFFFF3E0)
                        "Accepted" -> Color(0xFFE8F5E9)
                        else -> Color(0xFFFFEBEE)
                    }
                ) {
                    Text(
                        application.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when(application.status) {
                            "Pending" -> Color(0xFFEF6C00)
                            "Accepted" -> Color(0xFF2E7D32)
                            else -> Color(0xFFC62828)
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(application.appliedAt))
            Text("Applied on: $date", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun HeroHeader(
    userName: String,
    onBellClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(HeaderBlue)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Hello, $userName \uD83D\uDC4B",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ready to grow your career?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onBellClick,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Surface(
            onClick = onSearchClick,
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text("Search jobs, internships...", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ChipBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = HeaderBlue)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextButton(onClick = onSeeAllClick) { Text("See all →", color = HeaderBlue) }
    }
}

@Composable
private fun JobCard(job: Job, onClick: () -> Unit, onApply: (Job) -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Work, contentDescription = null, tint = HeaderBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(job.title, fontWeight = FontWeight.Bold)
                        Text(job.companyName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Text(job.salary, color = AccentGreen, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            
            val skills = job.requiredSkills.split(",").filter { it.isNotBlank() }
            if (skills.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    skills.take(3).forEach { SkillChip(it.trim()) }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.location + " • " + job.jobType,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Button(
                    onClick = { onApply(job) },
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue)
                ) {
                    Text("Apply", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun SkillChip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = ChipBg) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = HeaderBlue
        )
    }
}

@Composable
private fun InternshipBanner(onClick: () -> Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = AccentGreen), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("3 Internships Match You", color = Color.White, fontWeight = FontWeight.Bold)
            Text("→", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}