package com.example.ui.screens.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.WorkType
import com.example.ui.components.MatchAnalysisView
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    viewModel: JobDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "ATS Check", "Tailor Resume", "Cover Letter", "Copilot Q&A")

    val job = uiState.job

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.company, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSave() }, modifier = Modifier.testTag("detail_save_btn")) {
                        Icon(
                            imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (job.isSaved) JobSetuGoldAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleSave() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (job.isSaved) "Saved" else "Save")
                    }

                    Button(
                        onClick = {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(job.sourceUrl))
                            try {
                                context.startActivity(browserIntent)
                            } catch (_: Exception) {}
                            viewModel.onOpenExternalApply()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (job.isOfficialEmployerPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("open_official_apply_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (job.isOfficialEmployerPage) "Official Apply" else "Apply on ${job.sourceName}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("job_detail_content"),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Header Card: Title, Company, Badges, Salary, Location
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = job.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = job.company,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (job.isOfficialEmployerPage) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = VerifiedBadge.copy(alpha = 0.12f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = VerifiedBadge,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Official Employer Page",
                                                color = VerifiedBadge,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Key Attributes Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AttributePill(
                                    icon = Icons.Default.LocationOn,
                                    text = "${job.locationCity}, ${job.locationState}"
                                )
                                AttributePill(
                                    icon = Icons.Default.Work,
                                    text = job.workType.name.lowercase().replaceFirstChar { it.uppercase() }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val sal = if (job.maxSalaryLpa > 0) "₹${job.minSalaryLpa}–${job.maxSalaryLpa} LPA" else "Market Standard"
                                AttributePill(icon = Icons.Default.CurrencyRupee, text = sal)
                                AttributePill(
                                    icon = Icons.Default.Schedule,
                                    text = "${job.minExpYears}–${job.maxExpYears} Years Exp"
                                )
                            }

                            // Duplicate Sources Disclosure (Section 24)
                            if (job.duplicateSources.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Cross-Posted on ${job.duplicateSources.size + 1} sources:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    job.duplicateSources.forEach { source ->
                                        SuggestionChip(
                                            onClick = {
                                                try {
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.sourceUrl)))
                                                } catch (_: Exception) {}
                                            },
                                            label = { Text(source.sourceName, fontSize = 11.sp) }
                                        )
                                    }
                                }
                            }

                            // Scam Warning Alert (Section 25)
                            if (job.isScamWarning) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = ScamWarningOrange.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ScamWarningOrange.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Alert",
                                            tint = ScamWarningOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "⚠️ Needs Review — Trust & Safety Warning",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = ScamWarningOrange
                                            )
                                            Text(
                                                text = job.scamWarningReason,
                                                fontSize = 11.sp,
                                                color = ScamWarningOrange.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // TAB 0: OVERVIEW & MATCH ANALYSIS
                if (selectedTab == 0) {
                    if (uiState.matchAnalysis != null) {
                        item {
                            MatchAnalysisView(analysis = uiState.matchAnalysis!!)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item {
                        Text(text = "Job Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = job.description, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (job.responsibilities.isNotEmpty()) {
                        item {
                            Text(text = "Key Responsibilities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            job.responsibilities.forEach { resp ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", fontWeight = FontWeight.Bold)
                                    Text(text = resp, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (job.requirements.isNotEmpty()) {
                        item {
                            Text(text = "Requirements & Qualifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            job.requirements.forEach { req ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", fontWeight = FontWeight.Bold)
                                    Text(text = req, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (job.benefits.isNotEmpty()) {
                        item {
                            Text(text = "Perks & Benefits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            job.benefits.forEach { b ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("✓ ", color = MatchHighGreen, fontWeight = FontWeight.Bold)
                                    Text(text = b, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // TAB 1: ATS RESUME CHECK (Section 18)
                if (selectedTab == 1) {
                    item {
                        if (uiState.atsCheckResult == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Scan candidate profile against ${job.company}'s ATS requirements",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.runAtsResumeCheck() },
                                    enabled = !uiState.isAnalyzingResume,
                                    modifier = Modifier.testTag("run_ats_check_btn")
                                ) {
                                    if (uiState.isAnalyzingResume) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Analyzing ATS Keywords...")
                                    } else {
                                        Icon(Icons.Default.Speed, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Run ATS Resume Check")
                                    }
                                }
                            }
                        } else {
                            val res = uiState.atsCheckResult!!
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = "ATS Compatibility Score", fontWeight = FontWeight.Bold)
                                            Text(text = "Based on keywords & parser formatting", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            text = "${res.overallScore}/100",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 24.sp,
                                            color = MatchHighGreen
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "Matched ATS Keywords (✓):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = res.matchedKeywords.joinToString(" • "), color = MatchHighGreen, fontSize = 12.sp)

                                    if (res.missingHighPriorityKeywords.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(text = "Missing High-Priority Keywords (△):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = res.missingHighPriorityKeywords.joinToString(" • "), color = ScamWarningOrange, fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "Recommended Fixes:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    res.suggestedImprovements.forEach { fix ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                            Text("• ", fontWeight = FontWeight.Bold)
                                            Text(text = fix, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: TAILOR RESUME (Section 19)
                if (selectedTab == 2) {
                    item {
                        if (uiState.tailoredResume == null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Generate a tailored resume version customized strictly for ${job.title} at ${job.company}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.generateTailoredResume() },
                                    enabled = !uiState.isTailoringResume,
                                    modifier = Modifier.testTag("generate_tailored_resume_btn")
                                ) {
                                    if (uiState.isTailoringResume) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Tailoring Resume...")
                                    } else {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Generate Tailored Resume Diff")
                                    }
                                }
                            }
                        } else {
                            val resume = uiState.tailoredResume!!
                            Text(
                                text = "Proposed Resume Modifications (Strictly Factual):",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            resume.modificationsMade.forEach { diff ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Section: ${diff.section}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Original: \"${diff.originalText}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Proposed: \"${diff.proposedText}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "AI Rationale: ${diff.reasoning}", fontSize = 11.sp, color = JobSetuGoldAccent)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Saved to your Resume versions as '${resume.title}'",
                                fontSize = 12.sp,
                                color = MatchHighGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // TAB 3: COVER LETTER GENERATOR (Section 20)
                if (selectedTab == 3) {
                    item {
                        Column {
                            Text(text = "Select Cover Letter Tone:", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Professional", "Short", "Technical", "Enthusiastic").forEach { tone ->
                                    FilterChip(
                                        selected = uiState.selectedCoverLetterTone == tone,
                                        onClick = { viewModel.generateCoverLetter(tone) },
                                        label = { Text(tone, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.coverLetterResult == null && !uiState.isGeneratingLetter) {
                                Button(
                                    onClick = { viewModel.generateCoverLetter(uiState.selectedCoverLetterTone) },
                                    modifier = Modifier.testTag("generate_letter_btn")
                                ) {
                                    Icon(Icons.Default.Article, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Cover Letter")
                                }
                            }

                            if (uiState.isGeneratingLetter) {
                                CircularProgressIndicator()
                            }

                            if (uiState.coverLetterResult != null) {
                                val letter = uiState.coverLetterResult!!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Cover Letter (${letter.tone})", fontWeight = FontWeight.Bold)
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Cover Letter", letter.content))
                                                    Toast.makeText(context, "Cover letter copied to clipboard", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                            }
                                        }
                                        Divider()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = letter.content, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 4: APPLICATION COPILOT (Section 21)
                if (selectedTab == 4) {
                    item {
                        Column {
                            Text(
                                text = "Application Copilot — Answers for Online Portals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Copy verified answers directly into Workday, Lever, Greenhouse, or Naukri apply forms.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (uiState.copilotAnswers == null) {
                                Button(
                                    onClick = { viewModel.generateCopilotAnswers() },
                                    enabled = !uiState.isGeneratingCopilot,
                                    modifier = Modifier.testTag("generate_copilot_answers_btn")
                                ) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Portal Answers")
                                }
                            } else {
                                val ans = uiState.copilotAnswers!!
                                CopilotAnswerCard(context, "Why should we hire you?", ans.whyHireYou)
                                CopilotAnswerCard(context, "Tell us about yourself", ans.tellUsAboutYourself)
                                CopilotAnswerCard(context, "Why do you want to work at ${job.company}?", ans.whyThisCompany)
                                CopilotAnswerCard(context, "Expected CTC Justification", ans.expectedCtcExplanation)
                                CopilotAnswerCard(context, "Notice Period & Availability", ans.noticePeriodExplanation)
                                CopilotAnswerCard(context, "Relocation & Work Mode", ans.relocationWillingness)
                            }
                        }
                    }
                }
            }
        }
    }

    // Apply Outcome Confirmation Dialog (Section 22)
    if (uiState.showApplyOutcomeDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissApplyDialog() },
            title = { Text("Application Status Follow-up") },
            text = {
                Text(
                    "You opened the official application portal for ${job.company}. Did you complete and submit your application?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.recordApplicationOutcome(true)
                        Toast.makeText(context, "Application added to Tracker!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("confirm_applied_yes_btn")
                ) {
                    Text("Yes, Log to Tracker")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.recordApplicationOutcome(false) }
                ) {
                    Text("Not Yet")
                }
            }
        )
    }
}

@Composable
private fun CopilotAnswerCard(context: Context, question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = question, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Answer", answer))
                        Toast.makeText(context, "Answer copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = answer, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AttributePill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}
