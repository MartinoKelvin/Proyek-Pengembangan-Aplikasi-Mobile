package com.example.cakapAi.domain.model

/**
 * Domain model representing the progress state of a learning level.
 */
data class LevelProgress(
    val id: Int,
    val title: String,
    val subtitle: String,
    val levelType: String, // LISTENING, SPEAKING, VIDEO, READING, GAMING
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val highScore: Int,
    val updatedAt: Long
)
