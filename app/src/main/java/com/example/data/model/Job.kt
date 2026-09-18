package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val companyLogoUrl: String = "",
    val locationCity: String,
    val locationState: String,
    val workType: WorkType = WorkType.ON_SITE,
    val minExpYears: Int = 0,
    val maxExpYears: Int = 0,
    val minSalaryLpa: Double = 0.0,
    val maxSalaryLpa: Double = 0.0,
    val salaryMonthlyInr: Int = 0,
    val description: String,
    val responsibilities: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val skillsRequired: List<String> = emptyList(),
    val educationRequired: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val sourceName: String,
    val sourceUrl: String,
    val sourceClassification: SourceClassification = SourceClassification.EXTERNAL_APPLICATION,
    val isOfficialEmployerPage: Boolean = false,
    val postedTimeAgo: String = "Today",
    val postedTimestamp: Long = System.currentTimeMillis(),
    val isGovernment: Boolean = false,
    val isFresherEligible: Boolean = false,
    val isItRole: Boolean = true,
    val industry: String = "Technology",
    // Multi-source duplicate information
    val duplicateSources: List<JobDuplicateSource> = emptyList(),
    // Fake Job / Scam Protection
    val isScamWarning: Boolean = false,
    val scamWarningReason: String = "",
    // Pre-calculated or cached match info
    val matchPercentage: Int = 0,
    val isSaved: Boolean = false
)

data class JobDuplicateSource(
    val sourceName: String,
    val sourceUrl: String,
    val classification: SourceClassification,
    val isOfficial: Boolean
)

data class JobMatchAnalysis(
    val overallMatchPercent: Int,
    val skillsMatchPercent: Int,
    val experienceMatchPercent: Int,
    val educationMatchPercent: Int,
    val locationMatchPercent: Int,
    val salaryMatchPercent: Int,
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val alignmentNotes: String,
    val gapsExplanation: List<String>
)
