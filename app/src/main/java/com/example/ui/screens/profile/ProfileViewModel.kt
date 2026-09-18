package com.example.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIProvider
import com.example.data.model.CandidateProfile
import com.example.data.model.NoticePeriod
import com.example.data.model.ResumeVersion
import com.example.data.model.WorkType
import com.example.data.repository.CandidateRepository
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: CandidateProfile = CandidateProfile(),
    val resumes: List<ResumeVersion> = emptyList(),
    val isSaving: Boolean = false,
    val isParsing: Boolean = false,
    val showSuccessSnackbar: Boolean = false
)

class ProfileViewModel(
    private val candidateRepository: CandidateRepository,
    private val jobRepository: JobRepository,
    private val aiProvider: AIProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prof = candidateRepository.ensureInitialProfile()
            _uiState.update { it.copy(profile = prof) }

            candidateRepository.profileFlow.collect { p ->
                if (p != null) {
                    _uiState.update { it.copy(profile = p) }
                }
            }
        }

        viewModelScope.launch {
            candidateRepository.allResumesFlow.collect { list ->
                _uiState.update { it.copy(resumes = list) }
            }
        }
    }

    fun updateProfile(updated: CandidateProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            candidateRepository.saveProfile(updated)
            jobRepository.refreshJobs(updated)
            _uiState.update { it.copy(isSaving = false, showSuccessSnackbar = true) }
        }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(showSuccessSnackbar = false) }
    }

    fun loadSampleProfile(preset: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsing = true) }
            val newProfile = when (preset) {
                "mca_fresher" -> CandidateProfile(
                    id = "primary_candidate",
                    fullName = "Priya Sharma",
                    email = "priya.sharma@example.com",
                    phone = "+91 98401 23456",
                    currentRole = "Junior Software Developer Trainee",
                    desiredRoles = listOf("Junior Software Engineer", "Full Stack Developer", "Android Trainee"),
                    experienceYears = 0.0,
                    highestEducation = "MCA",
                    university = "Anna University",
                    graduationYear = 2024,
                    currentCity = "Bengaluru",
                    currentState = "Karnataka",
                    preferredCities = listOf("Bengaluru", "Chennai", "Hyderabad"),
                    technicalSkills = listOf("Java", "Kotlin", "Python", "SQL", "HTML/CSS", "Data Structures"),
                    softSkills = listOf("Quick Learner", "Problem Solving", "Team Collaboration"),
                    currentCtcLpa = 0.0,
                    expectedCtcLpa = 4.5,
                    noticePeriod = NoticePeriod.IMMEDIATE,
                    isServingNotice = false,
                    workPreference = WorkType.HYBRID,
                    summary = "Motivated MCA graduate (Anna University, 2024) with deep foundations in Java, Kotlin, and relational databases. Eager to contribute to enterprise engineering squads."
                )
                "accounts_exec" -> CandidateProfile(
                    id = "primary_candidate",
                    fullName = "Karthik Rajan",
                    email = "karthik.rajan@example.com",
                    phone = "+91 97910 88776",
                    currentRole = "Accounts & Tally Executive",
                    desiredRoles = listOf("Senior Accounts Officer", "Finance & Reconciliation Specialist"),
                    experienceYears = 2.5,
                    highestEducation = "B.Com",
                    university = "Madras University",
                    graduationYear = 2022,
                    currentCity = "Chennai",
                    currentState = "Tamil Nadu",
                    preferredCities = listOf("Chennai", "Coimbatore"),
                    technicalSkills = listOf("Tally Prime", "Advanced Excel", "GST Filing", "Bank Reconciliation", "MIS Reporting"),
                    softSkills = listOf("High Numerical Accuracy", "Audit Coordination", "Vendor Communication"),
                    currentCtcLpa = 3.5,
                    expectedCtcLpa = 5.2,
                    noticePeriod = NoticePeriod.DAYS_15,
                    isServingNotice = true,
                    workPreference = WorkType.ON_SITE,
                    summary = "Detail-oriented B.Com professional with 2.5 years experience in ledger maintenance, GST filings, and multi-bank reconciliations."
                )
                else -> CandidateProfile() // Default Arun Kumar Tour Ops
            }

            candidateRepository.saveProfile(newProfile)
            jobRepository.refreshJobs(newProfile)
            _uiState.update { it.copy(isParsing = false, showSuccessSnackbar = true) }
        }
    }

    fun parseRawResumeText(text: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsing = true) }
            val extracted = aiProvider.parseResume(text)
            candidateRepository.saveProfile(extracted)
            jobRepository.refreshJobs(extracted)
            _uiState.update { it.copy(isParsing = false, showSuccessSnackbar = true) }
        }
    }

    fun deleteResume(id: String) {
        viewModelScope.launch {
            candidateRepository.deleteResume(id)
        }
    }
}
