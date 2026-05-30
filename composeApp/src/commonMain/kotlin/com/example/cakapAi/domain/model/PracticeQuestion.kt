package com.example.cakapAi.domain.model

import kotlinx.serialization.Serializable

enum class PracticeQuestionType {
    MULTIPLE_CHOICE,
    FILL_BLANK,
    SPEAKING
}

@Serializable
data class PracticeQuestion(
    val id: String,
    val levelId: Int,
    val type: PracticeQuestionType,
    val instruction: String,
    val prompt: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String = ""
)
