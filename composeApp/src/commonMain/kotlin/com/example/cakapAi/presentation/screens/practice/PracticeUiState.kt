package com.example.cakapAi.presentation.screens.practice

import com.example.cakapAi.domain.model.PracticeQuestion

data class PracticeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val levelId: Int? = null,
    val questions: List<PracticeQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val typedAnswer: String = "",
    val spokenText: String = "",
    val isAnswerChecked: Boolean = false,
    val isCurrentAnswerCorrect: Boolean? = null,
    val correctCount: Int = 0,
    val lives: Int = 3,
    val isFinished: Boolean = false,
    val isUsingOfflineFallback: Boolean = false,
    val isListening: Boolean = false
) {
    val currentQuestion: PracticeQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
}
