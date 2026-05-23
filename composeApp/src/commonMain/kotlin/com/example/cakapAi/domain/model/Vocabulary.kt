package com.example.cakapAi.domain.model

/**
 * Domain model representing an offline vocabulary card.
 */
data class Vocabulary(
    val id: Long? = null,
    val levelId: Int,
    val word: String,
    val phonetic: String?,
    val definition: String,
    val example: String?
)
