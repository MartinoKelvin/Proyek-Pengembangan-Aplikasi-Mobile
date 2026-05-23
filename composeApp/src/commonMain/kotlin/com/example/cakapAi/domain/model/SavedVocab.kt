package com.example.cakapAi.domain.model

data class SavedVocab(
    val id: Int = 0,
    val sourceLang: String,
    val targetLang: String,
    val sourceText: String,
    val translatedText: String,
    val createdAt: Long = 0
)
