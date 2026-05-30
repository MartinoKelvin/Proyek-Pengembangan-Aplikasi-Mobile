package com.example.cakapAi.domain.repository

import com.example.cakapAi.domain.model.PracticeQuestion

interface PracticeRepository {
    suspend fun generatePracticeQuestions(levelId: Int, levelTitle: String, levelType: String): Result<List<PracticeQuestion>>
    suspend fun getOfflineFallbackQuestions(levelId: Int): List<PracticeQuestion>
}
