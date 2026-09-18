package com.example.data.repository

import com.example.data.ai.AIProvider
import com.example.data.local.JobDao
import com.example.data.model.CandidateProfile
import com.example.data.model.Job
import com.example.data.provider.AggregatorJobProvider
import com.example.data.provider.CompanyCareerProvider
import com.example.data.provider.DuplicateJobDetector
import com.example.data.provider.ScamProtectionEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class JobRepository(
    private val jobDao: JobDao,
    private val aiProvider: AIProvider,
    private val companyCareerProvider: CompanyCareerProvider = CompanyCareerProvider(),
    private val aggregatorJobProvider: AggregatorJobProvider = AggregatorJobProvider()
) {
    val allJobs: Flow<List<Job>> = jobDao.getAllJobs()
    val savedJobs: Flow<List<Job>> = jobDao.getSavedJobs()

    suspend fun refreshJobs(candidateProfile: CandidateProfile? = null) {
        // Production mode: only ingest jobs returned by real permitted APIs.
        // The previous CompanyCareerProvider contains demonstration fixtures and
        // must never be mixed into the live feed.
        val aggregatorJobs = aggregatorJobProvider.fetchJobs()
        val combined = aggregatorJobs

        // Consolidate duplicates across sources
        val consolidated = DuplicateJobDetector.consolidateDuplicates(combined)

        // Evaluate scam protections and calculate match scores with profile
        val enrichedJobs = consolidated.map { job ->
            val scamResult = ScamProtectionEngine.evaluateJob(
                title = job.title,
                company = job.company,
                description = job.description,
                salaryLpa = job.maxSalaryLpa,
                minExpYears = job.minExpYears,
                sourceUrl = job.sourceUrl
            )

            val matchAnalysis = if (candidateProfile != null) {
                aiProvider.matchJob(candidateProfile, job)
            } else null

            job.copy(
                isScamWarning = job.isScamWarning || scamResult.isWarning,
                scamWarningReason = if (job.scamWarningReason.isNotEmpty()) job.scamWarningReason else scamResult.explanation,
                matchPercentage = matchAnalysis?.overallMatchPercent ?: 0
            )
        }

        jobDao.insertJobs(enrichedJobs)
    }

    suspend fun getJobById(id: String): Job? {
        return jobDao.getJobById(id)
    }

    fun getJobByIdFlow(id: String): Flow<Job?> {
        return jobDao.getJobByIdFlow(id)
    }

    suspend fun toggleSaveJob(jobId: String, currentSaved: Boolean) {
        jobDao.updateSavedStatus(jobId, !currentSaved)
    }

    suspend fun updateJob(job: Job) {
        jobDao.updateJob(job)
    }

    suspend fun ensureInitialJobs(candidateProfile: CandidateProfile? = null) {
        if (jobDao.getCount() == 0) {
            refreshJobs(candidateProfile)
        }
    }
}
