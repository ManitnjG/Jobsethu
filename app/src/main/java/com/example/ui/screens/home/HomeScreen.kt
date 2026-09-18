package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Job
import com.example.ui.components.AiSearchBox
import com.example.ui.components.JobCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onJobClick: (Job) -> Unit,
    onApplyClick: (Job) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onNavigateToGovt: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "JobSetu AI",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        )
                        if (uiState.isDemoMode) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = JobSetuGoldAccent.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, JobSetuGoldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("demo_mode_badge")
                            ) {
                                Text(
                                    text = "Demo Data",
                                    color = JobSetuGoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToGovt,
                        modifier = Modifier.testTag("govt_jobs_action_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Government Jobs",
                            tint = GovtBadgeBlue
                        )
                    }
                    IconButton(
                        onClick = onNavigateToAlerts,
                        modifier = Modifier.testTag("alerts_action_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Job Alerts",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_action_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("home_screen_scroll"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Greeting Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(JobSetuNavyPrimary, JobSetuTealSecondary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "வணக்கம் / Welcome,",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = uiState.candidateName,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = JobSetuGoldDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Scam Guard Active",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Job Search & Application Assistant for India",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // AI Natural Search Box with Suggestions
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AiSearchBox(
                        query = searchInput,
                        onQueryChange = { searchInput = it },
                        onSearch = {
                            if (searchInput.isNotBlank()) {
                                onSearchTriggered(searchInput)
                            }
                        },
                        onSuggestionClick = { suggestion ->
                            searchInput = suggestion
                            onSearchTriggered(suggestion)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Govt Jobs Shortcut Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToGovt() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GovtBadgeBlue.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GovtBadgeBlue.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GovtBadgeBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Verified Indian Government Jobs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "UPSC • SSC • IBPS • Railways • State PSCs (TNPSC)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Explore",
                            tint = GovtBadgeBlue
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 1: 🔥 Best Matches (90%+ Profile Match)
            if (uiState.bestMatches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "🔥 Best Matches",
                        subtitle = "High alignment with candidate skills and experience",
                        count = uiState.bestMatches.size
                    )
                }
                items(uiState.bestMatches) { job ->
                    JobCard(
                        job = job,
                        onJobClick = onJobClick,
                        onSaveToggle = { viewModel.toggleSave(it) },
                        onApplyClick = onApplyClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Section 2: 🆕 Jobs Posted Today
            if (uiState.postedToday.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "🆕 Jobs Posted Today",
                        subtitle = "Fresh openings from official ATS & aggregator feeds",
                        count = uiState.postedToday.size
                    )
                }
                items(uiState.postedToday) { job ->
                    JobCard(
                        job = job,
                        onJobClick = onJobClick,
                        onSaveToggle = { viewModel.toggleSave(it) },
                        onApplyClick = onApplyClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Section 3: 🏠 Work From Home Opportunities
            if (uiState.workFromHomeJobs.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "🏠 Work From Home / Remote",
                        subtitle = "100% remote roles across India",
                        count = uiState.workFromHomeJobs.size
                    )
                }
                items(uiState.workFromHomeJobs) { job ->
                    JobCard(
                        job = job,
                        onJobClick = onJobClick,
                        onSaveToggle = { viewModel.toggleSave(it) },
                        onApplyClick = onApplyClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Section 4: 📍 In Preferred Cities (Chennai, Bengaluru, Coimbatore)
            if (uiState.nearPreferredJobs.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "📍 In Your Preferred Cities",
                        subtitle = "Openings in Chennai, Bengaluru, and Coimbatore",
                        count = uiState.nearPreferredJobs.size
                    )
                }
                items(uiState.nearPreferredJobs) { job ->
                    JobCard(
                        job = job,
                        onJobClick = onJobClick,
                        onSaveToggle = { viewModel.toggleSave(it) },
                        onApplyClick = onApplyClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Section 5: 🎓 Fresher & Entry-Level Opportunities
            if (uiState.fresherJobs.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "🎓 Fresher & Trainee Openings",
                        subtitle = "0–1 Year experience friendly",
                        count = uiState.fresherJobs.size
                    )
                }
                items(uiState.fresherJobs) { job ->
                    JobCard(
                        job = job,
                        onJobClick = onJobClick,
                        onSaveToggle = { viewModel.toggleSave(it) },
                        onApplyClick = onApplyClick,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    count: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (count != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
