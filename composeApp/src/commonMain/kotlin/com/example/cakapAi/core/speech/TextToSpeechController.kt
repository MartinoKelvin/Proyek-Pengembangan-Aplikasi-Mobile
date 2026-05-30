package com.example.cakapAi.core.speech

interface TextToSpeechController {
    fun speak(text: String)
    fun stop()
}
