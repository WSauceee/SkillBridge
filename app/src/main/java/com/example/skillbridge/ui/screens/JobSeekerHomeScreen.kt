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
import com.example.skillbridge.data.User
import kotlinx.coroutines.launch


private val HeaderBlue = Color(0xFF3169F0)
private val AccentGreen = Color(0xFF3DBE6B)
private val ChipBg = Color(0xFFE8EFFE)

private enum class BottomTab(val label: String) {
    HOME("Home"), JOBS("Jobs"), PROFILE("Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobSeekerHomeScreen(
    user: User,
    onResumeCreatorClick: () -> Unit,
    onAddEducation: (EducationEntry) -> Unit,
    onAddSkill: (String) -> Unit,
    onAddExperience: (ExperienceEntry) -> Unit,
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
                    onPlaceholderClick = { showComingSoon(it) },
                    onLogout = onLogout
                )
                BottomTab.PROFILE -> ProfileScreen(
                    user = user,
                    onAddEducation = onAddEducation,
                    onAddSkill = onAddSkill,
                    onAddExperience = onAddExperience,
                    onGenerateResumeClick = onResumeCreatorClick
                )
                BottomTab.JOBS -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Jobs — coming soon", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    user: User,
    onPlaceholderClick: (String) -> Unit,
    onLogout: () -> Unit
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
            SectionHeader("Recommended Job") { onPlaceholderClick("Recommended jobs") }
            Spacer(Modifier.height(12.dp))
            RecommendedJobCard(onClick = { onPlaceholderClick("Job details") })

            Spacer(Modifier.height(20.dp))
            InternshipBanner(onClick = { onPlaceholderClick("Internships") })
            Spacer(Modifier.height(20.dp))
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
private fun RecommendedJobCard(onClick: () -> Unit) {
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
                        Text("Software Developer", fontWeight = FontWeight.Bold)
                        Text("ABC Technology", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Text("RM3,500/mo", color = AccentGreen, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Java", "SQL", "React").forEach { SkillChip(it) }
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