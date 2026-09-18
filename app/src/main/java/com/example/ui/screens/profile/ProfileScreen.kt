package com.example.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandidateProfile
import com.example.data.model.NoticePeriod
import com.example.data.model.WorkType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val prof = uiState.profile

    var isEditing by remember { mutableStateOf(false) }
    var showRawResumeInput by remember { mutableStateOf(false) }
    var rawResumeText by remember { mutableStateOf("") }

    // Form fields state
    var name by remember(prof) { mutableStateOf(prof.fullName) }
    var phone by remember(prof) { mutableStateOf(prof.phone) }
    var email by remember(prof) { mutableStateOf(prof.email) }
    var currentRole by remember(prof) { mutableStateOf(prof.currentRole) }
    var expYears by remember(prof) { mutableStateOf(prof.experienceYears.toString()) }
    var education by remember(prof) { mutableStateOf(prof.highestEducation) }
    var city by remember(prof) { mutableStateOf(prof.currentCity) }
    var currentCtc by remember(prof) { mutableStateOf(prof.currentCtcLpa.toString()) }
    var expectedCtc by remember(prof) { mutableStateOf(prof.expectedCtcLpa.toString()) }
    var noticePeriod by remember(prof) { mutableStateOf(prof.noticePeriod) }
    var workType by remember(prof) { mutableStateOf(prof.workPreference) }
    var summary by remember(prof) { mutableStateOf(prof.summary) }

    val indianEducationLevels = listOf(
        "10th Standard", "12th Standard", "Diploma / ITI",
        "B.Sc Computer Science", "B.Com", "B.A.", "BBA", "BCA",
        "B.E. / B.Tech", "M.Sc", "M.Com", "MCA", "MBA", "M.E. / M.Tech"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Candidate Profile & Resumes",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    if (isEditing) {
                        TextButton(
                            onClick = {
                                val updated = prof.copy(
                                    fullName = name,
                                    phone = phone,
                                    email = email,
                                    currentRole = currentRole,
                                    experienceYears = expYears.toDoubleOrNull() ?: prof.experienceYears,
                                    highestEducation = education,
                                    currentCity = city,
                                    currentCtcLpa = currentCtc.toDoubleOrNull() ?: prof.currentCtcLpa,
                                    expectedCtcLpa = expectedCtc.toDoubleOrNull() ?: prof.expectedCtcLpa,
                                    noticePeriod = noticePeriod,
                                    workPreference = workType,
                                    summary = summary
                                )
                                viewModel.updateProfile(updated)
                                isEditing = false
                                Toast.makeText(context, "Profile updated & job matches recalculated!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("save_profile_btn")
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("profile_scroll_view"),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Preset Switcher Chips (Section 36 Demo Mode / Multi-persona testing)
            item {
                Text(text = "Quick Demo Personas (Switch Candidate):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { viewModel.loadSampleProfile("arun_ops") },
                        label = { Text("Arun (Tour Ops, 3 yrs)") }
                    )
                    SuggestionChip(
                        onClick = { viewModel.loadSampleProfile("mca_fresher") },
                        label = { Text("Priya (MCA Fresher)") }
                    )
                    SuggestionChip(
                        onClick = { viewModel.loadSampleProfile("accounts_exec") },
                        label = { Text("Karthik (Accounts, 2.5 yrs)") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Hero Candidate Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prof.fullName.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prof.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(text = prof.currentRole, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "${prof.currentCity}, ${prof.currentState} • ${prof.experienceYears.toInt()} yrs experience",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Notice Period", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(prof.noticePeriod.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JobSetuGoldAccent)
                            }
                            Column {
                                val monthlyEst = (prof.expectedCtcLpa * 100000 / 12).toInt()
                                Text("Expected CTC", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("₹${prof.expectedCtcLpa} LPA (₹$monthlyEst/mo)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MatchHighGreen)
                            }
                            Column {
                                Text("Education", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(prof.highestEducation, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Career Profile (Section 8: Likely Roles, Qualified Now vs After Upskilling)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = JobSetuGoldAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "AI Career Profile Intelligence", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Qualified Right Now (Immediate Match):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatchHighGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prof.desiredRoles.joinToString(" • "),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Possible With Targeted Upskilling (+30% Salary Jump):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = JobSetuGoldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Operations Lead • Corporate Travel Manager • Business Analyst (MIS)",
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Edit Profile Form or View
            if (isEditing) {
                item {
                    Text(text = "Edit Profile Attributes", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentRole,
                        onValueChange = { currentRole = it },
                        label = { Text("Current Role / Headline") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = expYears,
                            onValueChange = { expYears = it },
                            label = { Text("Exp (Years)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Current City") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = currentCtc,
                            onValueChange = { currentCtc = it },
                            label = { Text("Current CTC (₹ LPA)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = expectedCtc,
                            onValueChange = { expectedCtc = it },
                            label = { Text("Expected CTC (₹ LPA)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Notice Period Dropdown / Selector
                    Text("Notice Period:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NoticePeriod.values().forEach { np ->
                            FilterChip(
                                selected = noticePeriod == np,
                                onClick = { noticePeriod = np },
                                label = { Text(np.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = summary,
                        onValueChange = { summary = it },
                        label = { Text("Professional Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Section: Resumes (Master & Tailored Versions)
            item {
                Text(
                    text = "Resume Versions & Tailored Documents (${uiState.resumes.size})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.resumes) { resume ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = resume.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (resume.isMaster) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = "MASTER",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = resume.contentSummary,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }

                            if (!resume.isMaster) {
                                IconButton(onClick = { viewModel.deleteResume(resume.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Raw Resume Parser Expander
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { showRawResumeInput = !showRawResumeInput },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showRawResumeInput) "Hide Resume Text Parser" else "Paste & Parse New Resume")
                }

                if (showRawResumeInput) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rawResumeText,
                        onValueChange = { rawResumeText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Paste candidate bio, resume text, or LinkedIn summary here...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (rawResumeText.isNotBlank()) {
                                viewModel.parseRawResumeText(rawResumeText)
                                rawResumeText = ""
                                showRawResumeInput = false
                                Toast.makeText(context, "AI parsed candidate profile successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = rawResumeText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("AI Ingest & Extract Profile")
                    }
                }
            }
        }
    }
}
