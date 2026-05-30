package com.example.cakapAi.presentation.screens.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.core.speech.SpeechRecognizerController
import com.example.cakapAi.core.speech.TextToSpeechController
import com.example.cakapAi.core.speech.AudioFeedbackController
import com.example.cakapAi.domain.model.PracticeQuestionType
import com.example.cakapAi.domain.repository.PracticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val levelId: Int,
    private val levelTitle: String,
    private val levelType: String,
    private val repository: PracticeRepository,
    private val speechRecognizerController: SpeechRecognizerController,
    private val textToSpeechController: TextToSpeechController,
    private val audioFeedbackController: AudioFeedbackController
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState(levelId = levelId))
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.generatePracticeQuestions(levelId, levelTitle, levelType)
            if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = result.getOrNull()!!,
                        isUsingOfflineFallback = false
                    )
                }
            } else {
                val fallback = repository.getOfflineFallbackQuestions(levelId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = fallback,
                        isUsingOfflineFallback = true
                    )
                }
            }
        }
    }

    fun selectAnswer(answer: String) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun updateTypedAnswer(answer: String) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(typedAnswer = answer) }
    }

    fun updateSpokenTextFallback(answer: String) {
        if (_uiState.value.isAnswerChecked) return
        _uiState.update { it.copy(spokenText = answer) }
    }

    fun startListening() {
        if (!speechRecognizerController.isAvailable) {
            _uiState.update { it.copy(errorMessage = "Microphone tidak tersedia. Silakan ketik kalimat yang kamu ucapkan.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isListening = true, errorMessage = null) }
            val result = speechRecognizerController.startListening()
            _uiState.update { it.copy(isListening = false) }
            
            result.onSuccess { spoken ->
                _uiState.update { it.copy(spokenText = spoken) }
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }

    fun speak(text: String) {
        textToSpeechController.speak(text)
    }

    fun checkAnswer() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion ?: return
        
        val isCorrect = when (currentQuestion.type) {
            PracticeQuestionType.MULTIPLE_CHOICE -> 
                state.selectedAnswer == currentQuestion.correctAnswer
            PracticeQuestionType.FILL_BLANK ->
                state.typedAnswer.trim().equals(currentQuestion.correctAnswer, ignoreCase = true)
            PracticeQuestionType.SPEAKING -> {
                val normalizedSpoken = state.spokenText.trim().replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase()
                val normalizedCorrect = currentQuestion.correctAnswer.trim().replace(Regex("[^a-zA-Z0-9 ]"), "").lowercase()
                normalizedSpoken == normalizedCorrect
            }
        }

        val newLives = if (isCorrect) state.lives else state.lives - 1
        val newCorrectCount = if (isCorrect) state.correctCount + 1 else state.correctCount

        if (isCorrect) {
            audioFeedbackController.playCorrectSound()
        } else {
            audioFeedbackController.playIncorrectSound()
            audioFeedbackController.vibrateError()
        }

        _uiState.update {
            it.copy(
                isAnswerChecked = true,
                isCurrentAnswerCorrect = isCorrect,
                lives = newLives,
                correctCount = newCorrectCount
            )
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        
        if (state.lives <= 0) {
            _uiState.update { it.copy(isFinished = true) }
            return
        }

        if (state.currentQuestionIndex + 1 < state.questions.size) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    isAnswerChecked = false,
                    isCurrentAnswerCorrect = null,
                    selectedAnswer = null,
                    typedAnswer = "",
                    spokenText = "",
                    errorMessage = null
                )
            }
        } else {
            _uiState.update { it.copy(isFinished = true) }
        }
    }
}
