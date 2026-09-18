package com.example.data.local

import androidx.room.*
import com.example.data.model.Job
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY postedTimestamp DESC")
    fun getAllJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE isSaved = 1 ORDER BY postedTimestamp DESC")
    fun getSavedJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): Job?

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    fun getJobByIdFlow(id: String): Flow<Job?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<Job>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job)

    @Update
    suspend fun updateJob(job: Job)

    @Query("UPDATE jobs SET isSaved = :isSaved WHERE id = :jobId")
    suspend fun updateSavedStatus(jobId: String, isSaved: Boolean)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJobById(id: String)

    @Query("SELECT COUNT(*) FROM jobs")
    suspend fun getCount(): Int

    @Query("SELECT * FROM jobs")
    suspend fun getAllJobsSnapshot(): List<Job>

    @Query("DELETE FROM jobs WHERE isSaved = 0 AND postedTimestamp < :cutoffTimestamp")
    suspend fun deleteExpiredUnsavedJobs(cutoffTimestamp: Long): Int
}
