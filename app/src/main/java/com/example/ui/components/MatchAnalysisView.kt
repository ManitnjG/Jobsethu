package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobMatchAnalysis
import com.example.ui.theme.MatchHighGreen
import com.example.ui.theme.MatchMediumAmber

@Composable
fun MatchAnalysisView(
    analysis: JobMatchAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with overall percent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "AI Match Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (analysis.overallMatchPercent >= 80) "Strong Profile Fit" else "Moderate Profile Fit",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (analysis.overallMatchPercent >= 80) MatchHighGreen else MatchMediumAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = (if (analysis.overallMatchPercent >= 80) MatchHighGreen else MatchMediumAmber).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${analysis.overallMatchPercent}% Match",
                        color = if (analysis.overallMatchPercent >= 80) MatchHighGreen else MatchMediumAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-metrics Progress Bars
            MetricBar(label = "Skills Match", percent = analysis.skillsMatchPercent)
            MetricBar(label = "Experience Alignment", percent = analysis.experienceMatchPercent)
            MetricBar(label = "Education & Degree", percent = analysis.educationMatchPercent)
            MetricBar(label = "Location & Work Mode", percent = analysis.locationMatchPercent)
            MetricBar(label = "Salary & CTC", percent = analysis.salaryMatchPercent)

            Spacer(modifier = Modifier.height(14.dp))

            // Matched Attributes (✓)
            if (analysis.matchedSkills.isNotEmpty()) {
                Text(
                    text = "Matched Qualifications (✓):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MatchHighGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    analysis.matchedSkills.take(4).forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MatchHighGreen.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "✓ $skill",
                                color = MatchHighGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Gaps / Missing Attributes (△)
            if (analysis.missingSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Potential Gaps / Upskilling Needed (△):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MatchMediumAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    analysis.missingSkills.take(3).forEach { gap ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MatchMediumAmber.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "△ $gap",
                                color = MatchMediumAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandatory Disclaimer (Prompt Section 9)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Notice: Match percentage is an internal profile-match estimate based on public requirements, NOT a hiring guarantee or probability.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MetricBar(label: String, percent: Int) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (percent >= 80) MatchHighGreen else MatchMediumAmber,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        )
    }
}
