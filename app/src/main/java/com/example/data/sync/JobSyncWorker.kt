package com.example.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.ai.HybridAIService
import com.example.data.local.AppDatabase
import com.example.data.repository.JobRepository
import java.util.concurrent.TimeUnit

/**
 * Refreshes the real permitted job feed in the background.
 *
 * Android WorkManager's minimum periodic interval is 15 minutes. Android may
 * defer a run for battery/Doze reasons, so this is a best-effort device sync;
 * the UI can still refresh on demand.
 */
class JobSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            JobRepository(db.jobDao(), HybridAIService()).refreshJobs()
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK = "jobsethu_real_jobs_15m"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<JobSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
