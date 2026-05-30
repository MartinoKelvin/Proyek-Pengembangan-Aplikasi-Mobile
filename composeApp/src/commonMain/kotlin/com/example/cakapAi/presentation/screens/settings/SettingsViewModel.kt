package com.example.cakapAi.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * UI State representing the dynamic preferences state of the Settings Screen.
 */
sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val isDarkMode: Boolean) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

/**
 * ViewModel managing the user preferences (specifically Dark Mode) reactively via Datastore.
 */
class SettingsViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            userPreferences.isDarkMode
                .catch { e ->
                    _uiState.value = SettingsUiState.Error(e.message ?: "Gagal memuat pengaturan")
                }
                .collect { isDark ->
                    _uiState.value = SettingsUiState.Success(isDarkMode = isDark)
                }
        }
    }

    /**
     * Updates the dark theme preference.
     */
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setDarkMode(enabled)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Gagal memperbarui mode gelap")
            }
        }
    }
}
