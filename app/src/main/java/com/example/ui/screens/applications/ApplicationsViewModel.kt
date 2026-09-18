package com.example.ui.screens.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ApplicationStatus
import com.example.data.model.JobApplication
import com.example.data.repository.ApplicationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ApplicationsUiState(
    val applications: List<JobApplication> = emptyList(),
    val selectedStatusFilter: ApplicationStatus? = null,
    val totalCount: Int = 0,
    val appliedCount: Int = 0,
    val interviewCount: Int = 0,
    val offerCount: Int = 0
)

class ApplicationsViewModel(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApplicationsUiState())
    val uiState: StateFlow<ApplicationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allApplications.collect { apps ->
                val applied = apps.count { it.status == ApplicationStatus.APPLIED }
                val interviews = apps.count { it.status == ApplicationStatus.INTERVIEW }
                val offers = apps.count { it.status == ApplicationStatus.OFFER }

                _uiState.update {
                    it.copy(
                        applications = apps,
                        totalCount = apps.size,
                        appliedCount = applied,
                        interviewCount = interviews,
                        offerCount = offers
                    )
                }
            }
        }
    }

    fun filterByStatus(status: ApplicationStatus?) {
        _uiState.update { it.copy(selectedStatusFilter = status) }
    }

    fun updateStatus(app: JobApplication, newStatus: ApplicationStatus) {
        viewModelScope.launch {
            repository.updateStatus(app.id, app, newStatus)
        }
    }

    fun deleteApplication(id: String) {
        viewModelScope.launch {
            repository.deleteApplication(id)
        }
    }
}
