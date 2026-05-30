package com.example.cakapAi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.cakapAi.data.local.datastore.UserPreferences
import com.example.cakapAi.presentation.navigation.AppNavHost
import com.example.cakapAi.presentation.theme.cakapAiTheme
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val userPreferences: UserPreferences = koinInject()
        val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)

        cakapAiTheme(darkTheme = isDarkMode) {
            AppNavHost()
        }
    }
}
