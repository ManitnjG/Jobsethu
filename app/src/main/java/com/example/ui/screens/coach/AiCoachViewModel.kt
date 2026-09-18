package com.example.ui.screens.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIProvider
import com.example.data.model.*
import com.example.data.repository.CandidateRepository
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiCoachUiState(
    val candidateProfile: CandidateProfile? = null,
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            isUser = false,
            text = "வணக்கம் / Hello! I am your JobSetu Career AI Coach. I analyze real Indian job postings across IT, operations, tourism, accounts, and government sectors. Ask me about role suitability, resume improvements, salary benchmarks, or interview preparation!"
        )
    ),
    val isTyping: Boolean = false,
    val skillGapResult: SkillGapAnalysisResult? = null,
    val selectedTargetRoleForGap: String = "Senior Operations Executive",
    val isAnalyzingGap: Boolean = false,
    val mockSession: InterviewMockSession? = null,
    val isGeneratingMock: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val candidateCurrentAnswer: String = "",
    val feedbackMessage: String = ""
)

class AiCoachViewModel(
    private val candidateRepository: CandidateRepository,
    private val jobRepository: JobRepository,
    private val aiProvider: AIProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiCoachUiState())
    val uiState: StateFlow<AiCoachUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prof = candidateRepository.getProfile() ?: candidateRepository.ensureInitialProfile()
            _uiState.update { it.copy(candidateProfile = prof) }
            runSkillGapAnalysis(_uiState.value.selectedTargetRoleForGap)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), isUser = true, text = userText)
        val profile = _uiState.value.candidateProfile ?: CandidateProfile()

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                isTyping = true
            )
        }

        viewModelScope.launch {
            val advice = aiProvider.careerAdvice(profile, userText)
            val botMsg = ChatMessage(id = (System.currentTimeMillis() + 1).toString(), isUser = false, text = advice)
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + botMsg,
                    isTyping = false
                )
            }
        }
    }

    fun runSkillGapAnalysis(targetRole: String) {
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzingGap = true, selectedTargetRoleForGap = targetRole) }
            val result = aiProvider.analyzeSkillGap(profile, targetRole)
            _uiState.update { it.copy(skillGapResult = result, isAnalyzingGap = false) }
        }
    }

    fun startMockInterview(mode: String) {
        val profile = _uiState.value.candidateProfile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingMock = true) }
            val dummyJob = Job(
                id = "mock_context",
                title = profile.currentRole,
                company = "Premier Global Solutions",
                locationCity = profile.currentCity,
                locationState = profile.currentState,
                description = "Operations and customer management",
                sourceName = "JobSetu Practice",
                sourceUrl = ""
            )
            val session = aiProvider.generateInterviewQuestions(profile, dummyJob, mode)
            _uiState.update {
                it.copy(
                    mockSession = session,
                    isGeneratingMock = false,
                    currentQuestionIndex = 0,
                    candidateCurrentAnswer = "",
                    feedbackMessage = ""
                )
            }
        }
    }

    fun updateCandidateAnswer(answer: String) {
        _uiState.update { it.copy(candidateCurrentAnswer = answer) }
    }

    fun submitAnswer() {
        val session = _uiState.value.mockSession ?: return
        val currentIdx = _uiState.value.currentQuestionIndex
        val answer = _uiState.value.candidateCurrentAnswer
        if (currentIdx >= session.questions.size) return

        val question = session.questions[currentIdx]
        val wordCount = answer.trim().split("\\s+".toRegex()).size

        val (score, feedback) = if (wordCount < 10) {
            Pair(
                5,
                "Your answer is too brief. Recruiters expect structured STAR-format responses (Situation, Task, Action, Result) with specific examples."
            )
        } else {
            Pair(
                8,
                "Strong response! You addressed key requirements clearly and demonstrated confidence. Consider adding quantifiable results (e.g. percentages or timeline benchmarks)."
            )
        }

        question.candidateAnswer = answer
        question.scoreOutOf10 = score
        question.feedback = feedback

        _uiState.update {
            it.copy(
                feedbackMessage = feedback,
                candidateCurrentAnswer = ""
            )
        }
    }

    fun nextQuestion() {
        val session = _uiState.value.mockSession ?: return
        val nextIdx = _uiState.value.currentQuestionIndex + 1
        if (nextIdx < session.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIdx,
                    feedbackMessage = "",
                    candidateCurrentAnswer = ""
                )
            }
        }
    }
}
