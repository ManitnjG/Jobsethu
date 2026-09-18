package com.example.data.repository

import com.example.data.local.ApplicationDao
import com.example.data.local.GovtJobDao
import com.example.data.local.JobAlertDao
import com.example.data.model.ApplicationStatus
import com.example.data.model.GovtJob
import com.example.data.model.JobAlert
import com.example.data.model.JobApplication
import com.example.data.provider.GovtJobProvider
import kotlinx.coroutines.flow.Flow

class ApplicationRepository(
    private val applicationDao: ApplicationDao
) {
    val allApplications: Flow<List<JobApplication>> = applicationDao.getAllApplications()

    suspend fun insertApplication(app: JobApplication) = applicationDao.insertApplication(app)
    suspend fun updateApplication(app: JobApplication) = applicationDao.updateApplication(app)
    suspend fun deleteApplication(id: String) = applicationDao.deleteApplicationById(id)

    suspend fun updateStatus(id: String, currentApp: JobApplication, newStatus: ApplicationStatus, nextAction: String = "") {
        applicationDao.updateApplication(
            currentApp.copy(
                status = newStatus,
                nextAction = nextAction.ifEmpty { currentApp.nextAction }
            )
        )
    }
}

class GovtJobRepository(
    private val govtJobDao: GovtJobDao,
    private val provider: GovtJobProvider = GovtJobProvider()
) {
    val allGovtJobs: Flow<List<GovtJob>> = govtJobDao.getAllGovtJobs()

    suspend fun ensureInitialGovtJobs() {
        if (govtJobDao.getCount() == 0) {
            val jobs = provider.getVerifiedGovtJobs()
            govtJobDao.insertGovtJobs(jobs)
        }
    }
}

class AlertRepository(
    private val alertDao: JobAlertDao
) {
    val allAlerts: Flow<List<JobAlert>> = alertDao.getAllAlerts()

    suspend fun addAlert(alert: JobAlert) = alertDao.insertAlert(alert)
    suspend fun deleteAlert(id: String) = alertDao.deleteAlertById(id)
    suspend fun toggleAlert(id: String, currentActive: Boolean) = alertDao.updateAlertActive(id, !currentActive)
}
