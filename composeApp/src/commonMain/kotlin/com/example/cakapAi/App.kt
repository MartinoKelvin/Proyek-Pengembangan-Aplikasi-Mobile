package com.example.cakapAi

import androidx.compose.runtime.Composable
import com.example.cakapAi.presentation.navigation.AppNavHost
import com.example.cakapAi.presentation.theme.cakapAiTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        cakapAiTheme {
            AppNavHost()
        }
    }
}


