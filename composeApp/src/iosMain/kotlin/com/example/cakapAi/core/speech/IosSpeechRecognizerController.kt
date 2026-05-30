package com.example.cakapAi.core.speech

class IosSpeechRecognizerController : SpeechRecognizerController {
    override val isAvailable: Boolean = false

    override suspend fun startListening(): Result<String> {
        return Result.failure(Exception("Speech recognition not implemented yet for iOS"))
    }
}
