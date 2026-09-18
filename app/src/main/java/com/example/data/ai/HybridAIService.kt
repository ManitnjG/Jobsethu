package com.example.data.ai

import com.example.data.model.*
import com.example.BuildConfig

class HybridAIService(
    private val localProvider: LocalRuleAIProvider = LocalRuleAIProvider()
) : AIProvider {

    private val isGeminiConfigured: Boolean
        get() = try {
            val key = BuildConfig.GEMINI_API_KEY
            key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        } catch (_: Throwable) {
            false
        }

    override suspend fun parseResume(rawText: String): CandidateProfile {
        return localProvider.parseResume(rawText)
    }

    override suspend fun searchIntent(naturalQuery: String): ParsedSearchIntent {
        return localProvider.searchIntent(naturalQuery)
    }

    override suspend fun matchJob(candidate: CandidateProfile, job: Job): JobMatchAnalysis {
        return localProvider.matchJob(candidate, job)
    }

    override suspend fun analyzeResume(candidate: CandidateProfile, job: Job): AtsResumeCheckResult {
        return localProvider.analyzeResume(candidate, job)
    }

    override suspend fun tailorResume(
        masterResume: ResumeVersion,
        candidate: CandidateProfile,
        job: Job
    ): ResumeVersion {
        return localProvider.tailorResume(masterResume, candidate, job)
    }

    override suspend fun generateCoverLetter(
        candidate: CandidateProfile,
        job: Job,
        tone: String
    ): CoverLetterResult {
        return localProvider.generateCoverLetter(candidate, job, tone)
    }

    override suspend fun generateApplicationAnswers(
        candidate: CandidateProfile,
        job: Job
    ): ApplicationCopilotAnswers {
        return localProvider.generateApplicationAnswers(candidate, job)
    }

    override suspend fun careerAdvice(
        candidate: CandidateProfile,
        userMessage: String,
        jobContext: Job?
    ): String {
        return localProvider.careerAdvice(candidate, userMessage, jobContext)
    }

    override suspend fun analyzeSkillGap(
        candidate: CandidateProfile,
        targetRole: String
    ): SkillGapAnalysisResult {
        return localProvider.analyzeSkillGap(candidate, targetRole)
    }

    override suspend fun generateInterviewQuestions(
        candidate: CandidateProfile,
        job: Job,
        mode: String
    ): InterviewMockSession {
        return localProvider.generateInterviewQuestions(candidate, job, mode)
    }
}
