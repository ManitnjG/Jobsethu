package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIProvider
import com.example.data.local.AppPreferences
import com.example.data.model.CandidateProfile
import com.example.data.model.Job
import com.example.data.model.WorkType
import com.example.data.repository.CandidateRepository
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val candidateName: String = "Candidate",
    val candidateProfile: CandidateProfile? = null,
    val bestMatches: List<Job> = emptyList(),
    val postedToday: List<Job> = emptyList(),
    val recommendedJobs: List<Job> = emptyList(),
    val workFromHomeJobs: List<Job> = emptyList(),
    val nearPreferredJobs: List<Job> = emptyList(),
    val higherSalaryJobs: List<Job> = emptyList(),
    val fresherJobs: List<Job> = emptyList(),
    val itJobs: List<Job> = emptyList(),
    val nonItJobs: List<Job> = emptyList(),
    val savedJobs: List<Job> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val isDemoMode: Boolean = true,
    val isDataSaver: Boolean = false
)

class HomeViewModel(
    private val jobRepository: JobRepository,
    private val candidateRepository: CandidateRepository,
    private val aiProvider: AIProvider,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appPreferences.isDemoMode.collect { demo ->
                _uiState.update { it.copy(isDemoMode = demo) }
            }
        }
        viewModelScope.launch {
            appPreferences.isDataSaver.collect { ds ->
                _uiState.update { it.copy(isDataSaver = ds) }
            }
        }
        viewModelScope.launch {
            val profile = candidateRepository.ensureInitialProfile()
            jobRepository.ensureInitialJobs(profile)

            combine(
                jobRepository.allJobs,
                candidateRepository.profileFlow
            ) { jobs, prof ->
                val activeProf = prof ?: profile
                val best = jobs.filter { it.matchPercentage >= 85 }.sortedByDescending { it.matchPercentage }
                val today = jobs.filter { it.postedTimeAgo.contains("Today") || it.postedTimeAgo.contains("h ago") }
                val wfh = jobs.filter { it.workType == WorkType.REMOTE }
                val near = jobs.filter { job ->
                    activeProf.preferredCities.any { it.equals(job.locationCity, ignoreCase = true) } ||
                    activeProf.currentCity.equals(job.locationCity, ignoreCase = true)
                }
                val highSalary = jobs.filter { it.maxSalaryLpa >= 6.0 }.sortedByDescending { it.maxSalaryLpa }
                val freshers = jobs.filter { it.isFresherEligible || (it.minExpYears == 0 && it.maxExpYears <= 1) }
                val it = jobs.filter { it.isItRole }
                val nonIt = jobs.filter { !it.isItRole }
                val saved = jobs.filter { it.isSaved }

                HomeUiState(
                    candidateName = activeProf.fullName,
                    candidateProfile = activeProf,
                    bestMatches = best,
                    postedToday = today,
                    recommendedJobs = jobs.sortedByDescending { it.matchPercentage }.take(5),
                    workFromHomeJobs = wfh,
                    nearPreferredJobs = near,
                    higherSalaryJobs = highSalary,
                    fresherJobs = freshers,
                    itJobs = it,
                    nonItJobs = nonIt,
                    savedJobs = saved,
                    totalCount = jobs.size,
                    isLoading = false,
                    isDemoMode = appPreferences.isDemoMode.value,
                    isDataSaver = appPreferences.isDataSaver.value
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleSave(job: Job) {
        viewModelScope.launch {
            jobRepository.toggleSaveJob(job.id, job.isSaved)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val prof = candidateRepository.getProfile()
            jobRepository.refreshJobs(prof)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
