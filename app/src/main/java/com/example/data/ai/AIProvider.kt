package com.example.data.ai

import com.example.data.model.*

data class ParsedSearchIntent(
    val queryRole: String = "",
    val detectedCity: String = "",
    val detectedWorkType: WorkType? = null,
    val minExpYears: Int? = null,
    val maxExpYears: Int? = null,
    val minSalaryLpa: Double? = null,
    val onlyGovernment: Boolean = false,
    val onlyFresher: Boolean = false,
    val isPostedToday: Boolean = false,
    val reasoningExplanation: String = ""
)

interface AIProvider {
    suspend fun parseResume(rawText: String): CandidateProfile
    suspend fun searchIntent(naturalQuery: String): ParsedSearchIntent
    suspend fun matchJob(candidate: CandidateProfile, job: Job): JobMatchAnalysis
    suspend fun analyzeResume(candidate: CandidateProfile, job: Job): AtsResumeCheckResult
    suspend fun tailorResume(masterResume: ResumeVersion, candidate: CandidateProfile, job: Job): ResumeVersion
    suspend fun generateCoverLetter(candidate: CandidateProfile, job: Job, tone: String): CoverLetterResult
    suspend fun generateApplicationAnswers(candidate: CandidateProfile, job: Job): ApplicationCopilotAnswers
    suspend fun careerAdvice(candidate: CandidateProfile, userMessage: String, jobContext: Job? = null): String
    suspend fun analyzeSkillGap(candidate: CandidateProfile, targetRole: String): SkillGapAnalysisResult
    suspend fun generateInterviewQuestions(candidate: CandidateProfile, job: Job, mode: String): InterviewMockSession
}
