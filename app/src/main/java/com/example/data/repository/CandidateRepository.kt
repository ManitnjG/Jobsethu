package com.example.data.repository

import com.example.data.local.CandidateProfileDao
import com.example.data.local.ResumeDao
import com.example.data.model.CandidateProfile
import com.example.data.model.ResumeVersion
import kotlinx.coroutines.flow.Flow

class CandidateRepository(
    private val candidateProfileDao: CandidateProfileDao,
    private val resumeDao: ResumeDao
) {
    val profileFlow: Flow<CandidateProfile?> = candidateProfileDao.getProfileFlow()
    val allResumesFlow: Flow<List<ResumeVersion>> = resumeDao.getAllResumes()

    suspend fun getProfile(): CandidateProfile? = candidateProfileDao.getProfile()

    suspend fun saveProfile(profile: CandidateProfile) {
        candidateProfileDao.insertOrUpdate(profile)
    }

    suspend fun saveResume(resume: ResumeVersion) {
        resumeDao.insertResume(resume)
    }

    suspend fun deleteResume(id: String) {
        resumeDao.deleteResumeById(id)
    }

    suspend fun ensureInitialProfile(): CandidateProfile {
        val existing = candidateProfileDao.getProfile()
        if (existing != null) return existing

        val initial = CandidateProfile()
        candidateProfileDao.insertOrUpdate(initial)

        // Also create initial master resume
        val masterResume = ResumeVersion(
            id = "resume_master",
            title = "Master Resume",
            isMaster = true,
            targetRole = "Tour Operations & Customer Experience Executive",
            targetCompany = "General",
            contentSummary = initial.summary,
            skillsHighlighted = initial.technicalSkills + initial.softSkills
        )
        resumeDao.insertResume(masterResume)
        return initial
    }
}
