package com.example.cakapAi.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "Martino Kelvin",
    val email: String = "martinokelvin06032005@gmail.com"
)

class ProfileViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferences.userName,
        userPreferences.userEmail
    ) { name, email ->
        ProfileUiState(userName = name, email = email)
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
