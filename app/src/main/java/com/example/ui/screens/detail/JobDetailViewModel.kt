package com.example.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIProvider
import com.example.data.model.*
import com.example.data.repository.ApplicationRepository
import com.example.data.repository.CandidateRepository
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class JobDetailUiState(
    val job: Job? = null,
    val candidateProfile: CandidateProfile? = null,
    val matchAnalysis: JobMatchAnalysis? = null,
    val atsCheckResult: AtsResumeCheckResult? = null,
    val tailoredResume: ResumeVersion? = null,
    val coverLetterResult: CoverLetterResult? = null,
    val copilotAnswers: ApplicationCopilotAnswers? = null,
    val selectedCoverLetterTone: String = "Professional",
    val isAnalyzingResume: Boolean = false,
    val isTailoringResume: Boolean = false,
    val isGeneratingLetter: Boolean = false,
    val isGeneratingCopilot: Boolean = false,
    val showApplyOutcomeDialog: Boolean = false,
    val applicationRecorded: Boolean = false
)

class JobDetailViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository,
    private val candidateRepository: CandidateRepository,
    private val applicationRepository: ApplicationRepository,
    private val aiProvider: AIProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val job = jobRepository.getJobById(jobId)
            val profile = candidateRepository.getProfile() ?: candidateRepository.ensureInitialProfile()
            val match = if (job != null) aiProvider.matchJob(profile, job) else null

            _uiState.update {
                it.copy(
                    job = job,
                    candidateProfile = profile,
                    matchAnalysis = match
                )
            }
        }
    }

    fun toggleSave() {
        val currentJob = _uiState.value.job ?: return
        viewModelScope.launch {
            jobRepository.toggleSaveJob(currentJob.id, currentJob.isSaved)
            _uiState.update {
                it.copy(job = currentJob.copy(isSaved = !currentJob.isSaved))
            }
        }
    }

    fun runAtsResumeCheck() {
        val job = _uiState.value.job ?: return
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingResume = true) }
            val result = aiProvider.analyzeResume(profile, job)
            _uiState.update { it.copy(atsCheckResult = result, isAnalyzingResume = false) }
        }
    }

    fun generateTailoredResume() {
        val job = _uiState.value.job ?: return
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isTailoringResume = true) }
            val masterResume = ResumeVersion(
                id = "master_temp",
                title = "Master Resume",
                isMaster = true,
                contentSummary = profile.summary,
                skillsHighlighted = profile.technicalSkills
            )
            val tailored = aiProvider.tailorResume(masterResume, profile, job)
            candidateRepository.saveResume(tailored)
            _uiState.update { it.copy(tailoredResume = tailored, isTailoringResume = false) }
        }
    }

    fun generateCoverLetter(tone: String) {
        val job = _uiState.value.job ?: return
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingLetter = true, selectedCoverLetterTone = tone) }
            val letter = aiProvider.generateCoverLetter(profile, job, tone)
            _uiState.update { it.copy(coverLetterResult = letter, isGeneratingLetter = false) }
        }
    }

    fun generateCopilotAnswers() {
        val job = _uiState.value.job ?: return
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingCopilot = true) }
            val answers = aiProvider.generateApplicationAnswers(profile, job)
            _uiState.update { it.copy(copilotAnswers = answers, isGeneratingCopilot = false) }
        }
    }

    fun onOpenExternalApply() {
        _uiState.update { it.copy(showApplyOutcomeDialog = true) }
    }

    fun dismissApplyDialog() {
        _uiState.update { it.copy(showApplyOutcomeDialog = false) }
    }

    fun recordApplicationOutcome(appliedSuccessfully: Boolean) {
        val job = _uiState.value.job ?: return
        viewModelScope.launch {
            if (appliedSuccessfully) {
                val app = JobApplication(
                    id = "app_${UUID.randomUUID().toString().take(8)}",
                    jobId = job.id,
                    roleTitle = job.title,
                    companyName = job.company,
                    location = "${job.locationCity} (${job.workType.name})",
                    salaryText = if (job.maxSalaryLpa > 0) "₹${job.minSalaryLpa}–${job.maxSalaryLpa} LPA" else "Market Standard",
                    resumeVersionTitle = _uiState.value.tailoredResume?.title ?: "Master Resume",
                    applicationSource = job.sourceName,
                    destinationUrl = job.sourceUrl,
                    status = ApplicationStatus.APPLIED,
                    appliedTimestamp = System.currentTimeMillis(),
                    nextAction = "Follow up in 5 business days",
                    notes = "Applied via official channel."
                )
                applicationRepository.insertApplication(app)
                _uiState.update { it.copy(showApplyOutcomeDialog = false, applicationRecorded = true) }
            } else {
                _uiState.update { it.copy(showApplyOutcomeDialog = false) }
            }
        }
    }
}
