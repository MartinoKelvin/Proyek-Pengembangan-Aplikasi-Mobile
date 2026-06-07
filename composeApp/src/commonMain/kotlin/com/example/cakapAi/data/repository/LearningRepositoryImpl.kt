package com.example.cakapAi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.cakapAi.data.local.NoteDatabase
import com.example.cakapAi.data.local.toDomain
import com.example.cakapAi.domain.model.LevelProgress
import com.example.cakapAi.domain.model.QuizHistory
import com.example.cakapAi.domain.model.Vocabulary
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Concrete implementation of LearningRepository using SQLDelight queries in NoteDatabase.
 */
class LearningRepositoryImpl(
    private val database: NoteDatabase
) : LearningRepository {

    private val queries = database.cakapAiQueries

    override fun getAllLevels(): Flow<List<LevelProgress>> {
        return queries.getAllLevels()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getLevelById(levelId: Long): Flow<LevelProgress?> {
        return queries.getLevelById(levelId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun completeLevel(levelId: Long, score: Int, accuracy: Double) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                val currentLevel = queries.getLevelById(levelId).executeAsOneOrNull()
                if (currentLevel != null) {
                    val maxScore = maxOf(currentLevel.high_score, score.toLong())
                    queries.updateLevelProgress(
                        is_unlocked = 1L, // Stays unlocked
                        is_completed = 1L,
                        high_score = maxScore,
                        updated_at = Clock.System.now().toEpochMilliseconds(),
                        id = levelId
                    )
                }
            }
        }
    }

    override suspend fun unlockNextLevel(levelId: Long) {
        withContext(Dispatchers.IO) {
            queries.unlockNextLevel(levelId)
        }
    }

    override suspend fun insertQuizHistory(
        levelId: Long,
        score: Int,
        totalQuestions: Int,
        accuracy: Double,
        isPassed: Boolean
    ) {
        withContext(Dispatchers.IO) {
            queries.insertQuizHistory(
                level_id = levelId,
                score = score.toLong(),
                accuracy = accuracy,
                completed_at = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override fun getQuizHistory(levelId: Long): Flow<List<QuizHistory>> {
        return queries.getHistoryByLevel(levelId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override fun getVocabularies(levelId: Long): Flow<List<Vocabulary>> {
        return queries.getVocabsByLevel(levelId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun initializeLevelsIfEmpty() {
        withContext(Dispatchers.IO) {
            queries.transaction {
                val levels = queries.getAllLevels().executeAsList()
                if (levels.size < 20) {
                    queries.clearAllLevels()
                    queries.initLevels()
                }
            }
        }
    }
}
