package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateProfileDao {
    @Query("SELECT * FROM candidate_profile WHERE id = 'primary_candidate' LIMIT 1")
    fun getProfileFlow(): Flow<CandidateProfile?>

    @Query("SELECT * FROM candidate_profile WHERE id = 'primary_candidate' LIMIT 1")
    suspend fun getProfile(): CandidateProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: CandidateProfile)
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY appliedTimestamp DESC")
    fun getAllApplications(): Flow<List<JobApplication>>

    @Query("SELECT * FROM applications WHERE status = :status ORDER BY appliedTimestamp DESC")
    fun getApplicationsByStatus(status: ApplicationStatus): Flow<List<JobApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplication)

    @Update
    suspend fun updateApplication(application: JobApplication)

    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplicationById(id: String)
}

@Dao
interface GovtJobDao {
    @Query("SELECT * FROM government_jobs ORDER BY applicationEndDate ASC")
    fun getAllGovtJobs(): Flow<List<GovtJob>>

    @Query("SELECT * FROM government_jobs WHERE id = :id LIMIT 1")
    suspend fun getGovtJobById(id: String): GovtJob?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGovtJobs(jobs: List<GovtJob>)

    @Query("SELECT COUNT(*) FROM government_jobs")
    suspend fun getCount(): Int
}

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY isMaster DESC, createdAt DESC")
    fun getAllResumes(): Flow<List<ResumeVersion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeVersion)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteResumeById(id: String)

    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    suspend fun getResumeById(id: String): ResumeVersion?
}

@Dao
interface JobAlertDao {
    @Query("SELECT * FROM job_alerts ORDER BY isActive DESC")
    fun getAllAlerts(): Flow<List<JobAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: JobAlert)

    @Query("DELETE FROM job_alerts WHERE id = :id")
    suspend fun deleteAlertById(id: String)

    @Query("UPDATE job_alerts SET isActive = :isActive WHERE id = :id")
    suspend fun updateAlertActive(id: String, isActive: Boolean)
}
