package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class JobApplication(
    @PrimaryKey val id: String,
    val jobId: String,
    val roleTitle: String,
    val companyName: String,
    val location: String,
    val salaryText: String,
    val resumeVersionTitle: String,
    val applicationSource: String,
    val destinationUrl: String,
    val status: ApplicationStatus = ApplicationStatus.SAVED,
    val appliedTimestamp: Long = System.currentTimeMillis(),
    val nextAction: String = "",
    val notes: String = "",
    val interviewTimestamp: Long? = null,
    val interviewType: String = ""
)

@Entity(tableName = "job_alerts")
data class JobAlert(
    @PrimaryKey val id: String,
    val title: String,
    val locationCity: String,
    val minSalaryLpa: Double,
    val maxExperienceYears: Int,
    val onlyWorkFromHome: Boolean = false,
    val minMatchPercentThreshold: Int = 80,
    val isActive: Boolean = true,
    val lastNotifiedTimestamp: Long = System.currentTimeMillis()
)
