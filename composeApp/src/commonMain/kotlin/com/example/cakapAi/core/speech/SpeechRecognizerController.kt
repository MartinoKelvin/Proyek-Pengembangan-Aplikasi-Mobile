package com.example.cakapAi.core.speech

interface SpeechRecognizerController {
    val isAvailable: Boolean
    suspend fun startListening(): Result<String>
}
