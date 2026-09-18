package com.example.data.repository

import com.example.data.ai.AIProvider
import com.example.data.local.JobDao
import com.example.data.model.CandidateProfile
import com.example.data.model.Job
import com.example.data.provider.MultiSourceJobProvider
import com.example.data.provider.CompanyCareerProvider
import com.example.data.provider.DuplicateJobDetector
import com.example.data.provider.ScamProtectionEngine
import kotlinx.coroutines.flow.Flow

class JobRepository(
    private val jobDao: JobDao,
    private val aiProvider: AIProvider,
    private val companyCareerProvider: CompanyCareerProvider = CompanyCareerProvider(),
    private val realJobProvider: MultiSourceJobProvider = MultiSourceJobProvider()
) {
    val allJobs: Flow<List<Job>> = jobDao.getAllJobs()
    val savedJobs: Flow<List<Job>> = jobDao.getSavedJobs()

    suspend fun refreshJobs(candidateProfile: CandidateProfile? = null) {
        // Production mode: only ingest jobs returned by real permitted APIs.
        // The previous CompanyCareerProvider contains demonstration fixtures and
        // must never be mixed into the live feed.
        val combined = realJobProvider.fetchJobs()

        // Consolidate duplicates across sources
        val consolidated = DuplicateJobDetector.consolidateDuplicates(combined)

        // Preserve cached AI scores for unchanged jobs so a background refresh does
        // not repeatedly re-process the full database.
        val existingById = jobDao.getAllJobsSnapshot().associateBy { it.id }

        // Evaluate scam protections and calculate match scores only when needed.
        val enrichedJobs = consolidated.map { job ->
            val scamResult = ScamProtectionEngine.evaluateJob(
                title = job.title,
                company = job.company,
                description = job.description,
                salaryLpa = job.maxSalaryLpa,
                minExpYears = job.minExpYears,
                sourceUrl = job.sourceUrl
            )

            val existing = existingById[job.id]
            val materiallyChanged = existing == null ||
                existing.title != job.title ||
                existing.company != job.company ||
                existing.description != job.description ||
                existing.sourceUrl != job.sourceUrl ||
                existing.postedTimestamp != job.postedTimestamp

            val matchAnalysis = if (candidateProfile != null && materiallyChanged) {
                aiProvider.matchJob(candidateProfile, job)
            } else null

            job.copy(
                isScamWarning = job.isScamWarning || scamResult.isWarning,
                scamWarningReason = if (job.scamWarningReason.isNotEmpty()) job.scamWarningReason else scamResult.explanation,
                matchPercentage = matchAnalysis?.overallMatchPercent
                    ?: existing?.matchPercentage
                    ?: 0,
                isSaved = existing?.isSaved ?: job.isSaved
            )
        }

        if (enrichedJobs.isNotEmpty()) jobDao.insertJobs(enrichedJobs)

        // Keep saved jobs indefinitely; automatically remove stale unsaved listings.
        val expiryCutoff = System.currentTimeMillis() - 45L * 24 * 60 * 60 * 1000
        jobDao.deleteExpiredUnsavedJobs(expiryCutoff)
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
