package com.example.cakapAi.presentation.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State representing the database persistence phase of the Quiz Result Screen.
 */
sealed interface ResultUiState {
    data object Idle : ResultUiState
    data object Saving : ResultUiState
    data object Success : ResultUiState
    data class Error(val message: String) : ResultUiState
}

/**
 * ViewModel responsible for saving the completed quiz outcome into the database,
 * marking the active level as completed, and unlocking the subsequent level if passed.
 */
class ResultViewModel(
    private val repository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultUiState>(ResultUiState.Idle)
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    // Flag guard ensuring database saving runs exactly once under Compose recompositions
    private var hasSaved = false

    /**
     * Persists the quiz result into the offline database. If passed, updates progress
     * completion and unlocks the next curriculum level.
     */
    fun saveQuizResult(
        levelId: Long,
        score: Int,
        totalQuestions: Int,
        accuracy: Double,
        isPassed: Boolean
    ) {
        if (hasSaved) return
        hasSaved = true

        viewModelScope.launch {
            _uiState.value = ResultUiState.Saving
            try {
                // 1. Log quiz attempt in history database
                repository.insertQuizHistory(
                    levelId = levelId,
                    score = score,
                    totalQuestions = totalQuestions,
                    accuracy = accuracy,
                    isPassed = isPassed
                )

                // 2. Update level progress and unlock next level if passed
                if (isPassed) {
                    repository.completeLevel(
                        levelId = levelId,
                        score = score,
                        accuracy = accuracy
                    )
                    // Unlock the next level (levelId + 1)
                    repository.unlockNextLevel(levelId = levelId + 1)
                }

                _uiState.value = ResultUiState.Success
            } catch (e: Exception) {
                _uiState.value = ResultUiState.Error(
                    message = e.message ?: "Terjadi kesalahan saat menyimpan hasil belajar"
                )
            }
        }
    }
}
