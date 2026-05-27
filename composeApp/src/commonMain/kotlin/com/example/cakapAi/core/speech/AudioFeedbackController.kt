package com.example.cakapAi.core.speech

interface AudioFeedbackController {
    fun playCorrectSound()
    fun playIncorrectSound()
    fun vibrateError()
}
