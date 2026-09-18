package com.example.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIProvider
import com.example.data.ai.ParsedSearchIntent
import com.example.data.model.CandidateProfile
import com.example.data.model.Job
import com.example.data.model.WorkType
import com.example.data.repository.CandidateRepository
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchFilterState(
    val selectedCity: String = "",
    val selectedWorkType: WorkType? = null,
    val isFresherOnly: Boolean = false,
    val isItOnly: Boolean? = null, // null = all, true = IT, false = Non-IT
    val minSalaryLpa: Double = 0.0,
    val onlyOfficialPortals: Boolean = false,
    val sortByMatch: Boolean = true
)

data class SearchUiState(
    val searchQuery: String = "",
    val parsedIntent: ParsedSearchIntent? = null,
    val filters: SearchFilterState = SearchFilterState(),
    val searchResults: List<Job> = emptyList(),
    val totalCount: Int = 0,
    val isSearching: Boolean = false
)

class SearchViewModel(
    private val jobRepository: JobRepository,
    private val candidateRepository: CandidateRepository,
    private val aiProvider: AIProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var allJobsCache: List<Job> = emptyList()
    private var candidateProfileCache: CandidateProfile? = null

    init {
        viewModelScope.launch {
            candidateRepository.profileFlow.collect { prof ->
                candidateProfileCache = prof
            }
        }

        viewModelScope.launch {
            jobRepository.allJobs.collect { jobs ->
                allJobsCache = jobs
                applyFiltersAndSearch(_uiState.value.searchQuery, _uiState.value.filters)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun executeSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchQuery = query) }
            val intent = if (query.isNotBlank()) {
                aiProvider.searchIntent(query)
            } else null

            // Query live providers for every explicit search instead of only filtering cached jobs.
            val liveLocation = intent?.detectedCity?.takeIf { it.isNotBlank() }
                ?: _uiState.value.filters.selectedCity.takeIf { it.isNotBlank() }
            runCatching {
                jobRepository.refreshJobs(candidateProfileCache, query.takeIf { it.isNotBlank() }, liveLocation)
            }

            // Update filters based on parsed intent
            val updatedFilters = if (intent != null) {
                _uiState.value.filters.copy(
                    selectedCity = intent.detectedCity.ifEmpty { _uiState.value.filters.selectedCity },
                    selectedWorkType = intent.detectedWorkType ?: _uiState.value.filters.selectedWorkType,
                    isFresherOnly = intent.onlyFresher || _uiState.value.filters.isFresherOnly,
                    minSalaryLpa = intent.minSalaryLpa ?: _uiState.value.filters.minSalaryLpa
                )
            } else _uiState.value.filters

            _uiState.update { it.copy(parsedIntent = intent, filters = updatedFilters) }
            applyFiltersAndSearch(query, updatedFilters)
            _uiState.update { it.copy(isSearching = false) }
        }
    }

    fun updateFilters(newFilters: SearchFilterState) {
        _uiState.update { it.copy(filters = newFilters) }
        applyFiltersAndSearch(_uiState.value.searchQuery, newFilters)
    }

    fun resetFilters() {
        val defaultFilters = SearchFilterState()
        _uiState.update { it.copy(filters = defaultFilters, parsedIntent = null) }
        applyFiltersAndSearch(_uiState.value.searchQuery, defaultFilters)
    }

    fun toggleSave(job: Job) {
        viewModelScope.launch {
            jobRepository.toggleSaveJob(job.id, job.isSaved)
        }
    }

    private fun applyFiltersAndSearch(query: String, filters: SearchFilterState) {
        val lowerQuery = query.lowercase().trim()
        val intent = _uiState.value.parsedIntent

        val filtered = allJobsCache.filter { job ->
            // Query filter
            val matchesQuery = if (lowerQuery.isBlank()) true else {
                job.title.contains(lowerQuery, ignoreCase = true) ||
                job.company.contains(lowerQuery, ignoreCase = true) ||
                job.description.contains(lowerQuery, ignoreCase = true) ||
                job.skillsRequired.any { it.contains(lowerQuery, ignoreCase = true) } ||
                (intent?.queryRole?.isNotEmpty() == true && job.title.contains(intent.queryRole, ignoreCase = true))
            }

            // City filter
            val matchesCity = if (filters.selectedCity.isBlank()) true else {
                job.locationCity.equals(filters.selectedCity, ignoreCase = true) ||
                job.workType == WorkType.REMOTE
            }

            // Work type filter
            val matchesWorkType = if (filters.selectedWorkType == null) true else {
                job.workType == filters.selectedWorkType
            }

            // Fresher filter
            val matchesFresher = if (!filters.isFresherOnly) true else {
                job.isFresherEligible || (job.minExpYears == 0 && job.maxExpYears <= 1)
            }

            // IT / Non-IT
            val matchesIt = when (filters.isItOnly) {
                true -> job.isItRole
                false -> !job.isItRole
                null -> true
            }

            // Salary filter
            val matchesSalary = if (filters.minSalaryLpa <= 0.0) true else {
                job.maxSalaryLpa >= filters.minSalaryLpa
            }

            // Official portals only
            val matchesOfficial = if (!filters.onlyOfficialPortals) true else {
                job.isOfficialEmployerPage
            }

            matchesQuery && matchesCity && matchesWorkType && matchesFresher && matchesIt && matchesSalary && matchesOfficial
        }

        val sorted = if (filters.sortByMatch) {
            filtered.sortedByDescending { it.matchPercentage }
        } else {
            filtered.sortedByDescending { it.postedTimestamp }
        }

        _uiState.update {
            it.copy(
                searchResults = sorted,
                totalCount = sorted.size
            )
        }
    }
}
