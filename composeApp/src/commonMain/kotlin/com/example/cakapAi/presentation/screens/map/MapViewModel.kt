package com.example.cakapAi.presentation.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.domain.model.LevelProgress
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State representing all possible presentation states of the Map Screen.
 */
sealed interface MapUiState {
    data object Loading : MapUiState
    data class Success(val levels: List<LevelProgress>) : MapUiState
    data object Empty : MapUiState
    data class Error(val message: String) : MapUiState
}

/**
 * ViewModel managing the learning path map state, handling initial data load,
 * database setup, and reactive database state updates to the UI layer.
 */
class MapViewModel(
    private val repository: LearningRepository
) : ViewModel() {

    // Reactively maps database changes to screen UI states
    val uiState: StateFlow<MapUiState> = repository.getAllLevels()
        .map { levels ->
            if (levels.isEmpty()) {
                MapUiState.Empty
            } else {
                MapUiState.Success(levels)
            }
        }
        .catch { exception ->
            emit(MapUiState.Error(exception.message ?: "Terjadi kesalahan sistem saat memuat data"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MapUiState.Loading
        )

    init {
        initializeDefaultLevels()
    }

    /**
     * Guarantees default level paths are populated inside the local database on startup.
     */
    private fun initializeDefaultLevels() {
        viewModelScope.launch {
            try {
                repository.initializeLevelsIfEmpty()
            } catch (e: Exception) {
                // Handled gracefully; database flow catch block will handle presenting errors if query fails.
            }
        }
    }

    fun savePracticeResult(levelId: Int, score: Int, isPassed: Boolean) {
        viewModelScope.launch {
            if (isPassed) {
                repository.completeLevel(levelId.toLong(), score, 100.0)
                repository.unlockNextLevel(levelId.toLong() + 1)
            }
            repository.insertQuizHistory(levelId.toLong(), score, 5, 100.0, isPassed)
        }
    }
}
