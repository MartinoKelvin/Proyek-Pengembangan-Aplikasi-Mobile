package com.example.cakapAi.domain.repository

import com.example.cakapAi.domain.model.LevelProgress
import com.example.cakapAi.domain.model.QuizHistory
import com.example.cakapAi.domain.model.Vocabulary
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository representing the domain layer contract for offline-first learning progress,
 * quiz score history, and offline vocabulary resources.
 */
interface LearningRepository {
    
    /**
     * Retrieves all level progress entities dynamically from the database.
     */
    fun getAllLevels(): Flow<List<LevelProgress>>

    /**
     * Retrieves details of a specific level by its ID.
     */
    fun getLevelById(levelId: Long): Flow<LevelProgress?>

    /**
     * Marks a level as completed, saves the high score, and updates timestamp.
     */
    suspend fun completeLevel(levelId: Long, score: Int, accuracy: Double)

    /**
     * Unlocks the next subsequent level when the current level is passed.
     */
    suspend fun unlockNextLevel(levelId: Long)

    /**
     * Inserts a record of completed quiz performance into history.
     */
    suspend fun insertQuizHistory(
        levelId: Long,
        score: Int,
        totalQuestions: Int,
        accuracy: Double,
        isPassed: Boolean
    )

    /**
     * Retrieves all quiz completion history records for a specific level.
     */
    fun getQuizHistory(levelId: Long): Flow<List<QuizHistory>>

    /**
     * Retrieves all downloaded offline vocabulary words for a specific level.
     */
    fun getVocabularies(levelId: Long): Flow<List<Vocabulary>>

    /**
     * Initializes default curriculum levels into SQLDelight if database is currently empty.
     */
    suspend fun initializeLevelsIfEmpty()
}
