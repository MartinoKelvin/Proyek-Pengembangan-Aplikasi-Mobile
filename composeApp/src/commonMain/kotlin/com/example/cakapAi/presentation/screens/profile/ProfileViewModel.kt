package com.example.cakapAi.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.data.local.datastore.UserPreferences
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Martino Kelvin",
    val email: String = "martinokelvin06032005@gmail.com",
    val currentLevelTitle: String = "Loading...",
    val completedLevelCount: Int = 0,
    val totalLevelsCount: Int = 20
)

class ProfileViewModel(
    private val userPreferences: UserPreferences,
    private val learningRepository: LearningRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferences.userName,
        userPreferences.userEmail,
        learningRepository.getAllLevels()
    ) { name, email, levels ->
        val completedCount = levels.count { it.isCompleted }
        
        // Find the first level that is unlocked but NOT completed.
        // If all are completed or none matches, take the highest unlocked level or default.
        val currentLevelProgress = levels
            .filter { it.isUnlocked && !it.isCompleted }
            .minByOrNull { it.id }
            ?: levels.filter { it.isUnlocked }.maxByOrNull { it.id }
            ?: levels.firstOrNull()

        val levelTitle = if (currentLevelProgress != null) {
            currentLevelProgress.title
        } else {
            "Level 1 - Basic Greeting"
        }

        ProfileUiState(
            userName = name,
            email = email,
            currentLevelTitle = levelTitle,
            completedLevelCount = completedCount,
            totalLevelsCount = if (levels.isEmpty()) 20 else levels.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            userPreferences.saveProfile(name, email)
        }
    }
}
