package com.example.cakapAi.domain.model

/**
 * Domain model representing the score performance and history of a completed quiz.
 */
data class QuizHistory(
    val id: Long? = null,
    val levelId: Int,
    val score: Int,
    val accuracy: Double,
    val completedAt: Long
)
