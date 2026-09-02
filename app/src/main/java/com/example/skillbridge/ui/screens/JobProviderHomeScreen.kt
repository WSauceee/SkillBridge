package com.example.skillbridge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import com.example.skillbridge.data.Job
import com.example.skillbridge.data.User
import kotlinx.coroutines.launch

// Colors matched to the seeker design
private val HeaderBlue = Color(0xFF3169F0)
private val AccentGreen = Color(0xFF3DBE6B)
private val ChipBg = Color(0xFFE8EFFE)
private val DangerRed = Color(0xFFE53935)

private enum class ProviderTab(val label: String) {
    DASHBOARD("Dashboard"), PROFILE("Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobProviderHomeScreen(
    user: User,
    jobs: List<Job>,
    onPostJobClick: () -> Unit,
    onDeleteJob: (Job) -> Unit,
    onLogout: () -> Unit,
    onUpdateProfile: (String, String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(ProviderTab.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun showMessage(msg: String) {
        scope.launch { snackbarHostState.showSnackbar(msg) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == ProviderTab.DASHBOARD,
                    onClick = { selectedTab = ProviderTab.DASHBOARD },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = selectedTab == ProviderTab.PROFILE,
                    onClick = { selectedTab = ProviderTab.PROFILE },
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == ProviderTab.DASHBOARD) {
                ExtendedFloatingActionButton(
                    onClick = onPostJobClick,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Post Job") },
                    containerColor = AccentGreen,
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                ProviderTab.DASHBOARD -> DashboardTabContent(
                    user = user,
                    jobs = jobs,
                    onDeleteJob = onDeleteJob,
                    onLogout = onLogout,
                    onPlaceholderClick = { showMessage(it) }
                )
                ProviderTab.PROFILE -> CompanyProfileContent(
                    user = user,
                    onUpdateProfile = { desc, loc, web ->
                        onUpdateProfile(desc, loc, web)
                        showMessage("Profile updated successfully")
                    }
                )
            }
        }
    }
}

@Composable
private fun CompanyProfileContent(
    user: User,
    onUpdateProfile: (String, String, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf(user.companyDescription ?: "") }
    var location by remember { mutableStateOf(user.companyLocation ?: "") }
    var website by remember { mutableStateOf(user.companyWebsite ?: "") }

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
            Text("Company Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text("Manage your company details for candidates", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
        }

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Basic Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        if (!isEditing) {
                            TextButton(onClick = { isEditing = true }) {
                                Text("Edit", color = HeaderBlue)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    if (isEditing) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Company Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Headquarters Location") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { 
                                    isEditing = false
                                    description = user.companyDescription ?: ""
                                    location = user.companyLocation ?: ""
                                    website = user.companyWebsite ?: ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    onUpdateProfile(description, location, website)
                                    isEditing = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                            ) {
                                Text("Save Changes")
                            }
                        }
                    } else {
                        ProfileInfoItem("Company Name", user.fullName)
                        ProfileInfoItem("Industry", user.extraInfo ?: "Not specified")
                        ProfileInfoItem("Location", user.companyLocation ?: "Not specified")
                        ProfileInfoItem("Website", user.companyWebsite ?: "Not specified")
                        Spacer(Modifier.height(8.dp))
                        Text("About Us", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            user.companyDescription ?: "Add a description to tell candidates about your company.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (user.companyDescription == null) Color.Gray else Color.Unspecified
                        )
                    }
                }
            }
            
            // Stats summary in profile too
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChipBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Work, contentDescription = null, tint = HeaderBlue)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Active Engagement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Your company is visible to job seekers", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DashboardTabContent(
    user: User,
    jobs: List<Job>,
    onDeleteJob: (Job) -> Unit,
    onLogout: () -> Unit,
    onPlaceholderClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroHeader(
            userName = user.fullName,
            onLogout = onLogout,
            onBellClick = { onPlaceholderClick("Notifications") }
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))

            StatCard(Icons.Filled.Work, jobs.size.toString(), "Jobs Posted", Modifier.fillMaxWidth())

            Spacer(Modifier.height(24.dp))
            SectionHeader("Manage My Jobs") { /* Could navigate to a full list if needed */ }
            Spacer(Modifier.height(12.dp))

            if (jobs.isEmpty()) {
                EmptyJobsState()
            } else {
                jobs.forEach { job ->
                    JobProviderJobCard(
                        job = job,
                        onDeleteClick = { onDeleteJob(job) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HeroHeader(userName: String, onLogout: () -> Unit, onBellClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome back,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    userName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
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
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                }
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
    }
}

@Composable
private fun JobProviderJobCard(job: Job, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(job.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Text(job.salary, color = AccentGreen, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(Modifier.height(8.dp))
            Text(job.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
            
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    job.requiredSkills.split(",").take(3).forEach { skill ->
                        if (skill.isNotBlank()) {
                            SkillChip(skill.trim())
                        }
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = DangerRed)
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = HeaderBlue
        )
    }
}

@Composable
private fun EmptyJobsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.Work, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(Modifier.height(16.dp))
        Text("No jobs posted yet", color = Color.Gray, fontWeight = FontWeight.Medium)
        Text("Start by clicking 'Post Job'", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
    }
}