package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "government_jobs")
data class GovtJob(
    @PrimaryKey val id: String,
    val organization: String, // UPSC, SSC, IBPS, RRB, TNPSC, etc.
    val postTitle: String,
    val totalVacancies: Int,
    val qualification: String,
    val ageRequirements: String,
    val applicationStartDate: String,
    val applicationEndDate: String,
    val examDate: String,
    val applicationFee: String,
    val location: String,
    val officialNotificationPdfUrl: String,
    val officialApplyPortalUrl: String,
    val category: String = "Central", // Central, State, Banking, Railway, Defence
    val isVerifiedSource: Boolean = true
)

data class InterviewMockSession(
    val id: String,
    val jobTitle: String,
    val company: String,
    val mode: String, // HR, Technical, Managerial
    val questions: List<InterviewQuestion>
)

data class InterviewQuestion(
    val id: Int,
    val question: String,
    val expectedKeyPoints: List<String>,
    var candidateAnswer: String = "",
    var feedback: String = "",
    var scoreOutOf10: Int = 0
)

data class SkillGapAnalysisResult(
    val targetRole: String,
    val candidateDemonstratedSkills: List<String>,
    val partialSkills: List<String>,
    val missingHighDemandSkills: List<String>,
    val recommendedLearningPath: List<SkillLearningItem>
)

data class SkillLearningItem(
    val skillName: String,
    val importance: String, // Essential, Recommended, Good to have
    val rationale: String,
    val estimatedTimeToLearn: String
)
