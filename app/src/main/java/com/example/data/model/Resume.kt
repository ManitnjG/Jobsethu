package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class ResumeVersion(
    @PrimaryKey val id: String,
    val title: String, // e.g., "Master Resume", "Targeted – Operations Executive", "Targeted – Travel Consultant"
    val isMaster: Boolean = false,
    val targetRole: String = "",
    val targetCompany: String = "",
    val contentSummary: String,
    val skillsHighlighted: List<String>,
    val modificationsMade: List<ResumeModificationDiff> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ResumeModificationDiff(
    val section: String,
    val originalText: String,
    val proposedText: String,
    val reasoning: String,
    val isAccepted: Boolean = true
)

data class AtsResumeCheckResult(
    val overallScore: Int,
    val atsKeywordCoveragePercent: Int,
    val matchedKeywords: List<String>,
    val missingHighPriorityKeywords: List<String>,
    val formattingWarnings: List<String>,
    val suggestedImprovements: List<String>
)

data class CoverLetterResult(
    val tone: String, // Short, Professional, Technical, Enthusiastic
    val content: String,
    val targetRole: String,
    val targetCompany: String
)

data class ApplicationCopilotAnswers(
    val whyHireYou: String,
    val tellUsAboutYourself: String,
    val whyThisCompany: String,
    val expectedCtcExplanation: String,
    val noticePeriodExplanation: String,
    val relocationWillingness: String
)
