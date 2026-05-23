package com.example.cakapAi.data.local

import com.example.cakapAi.domain.model.LevelProgress
import com.example.cakapAi.domain.model.QuizHistory
import com.example.cakapAi.domain.model.Vocabulary

/**
 * Extension function to map LevelProgressEntity (SQLDelight) to LevelProgress (Domain Model).
 */
fun LevelProgressEntity.toDomain(): LevelProgress {
    return LevelProgress(
        id = id.toInt(),
        title = title,
        subtitle = subtitle,
        levelType = level_type,
        isUnlocked = is_unlocked == 1L,
        isCompleted = is_completed == 1L,
        highScore = high_score.toInt(),
        updatedAt = updated_at
    )
}

/**
 * Extension function to map QuizHistoryEntity (SQLDelight) to QuizHistory (Domain Model).
 */
fun QuizHistoryEntity.toDomain(): QuizHistory {
    return QuizHistory(
        id = id,
        levelId = level_id.toInt(),
        score = score.toInt(),
        accuracy = accuracy,
        completedAt = completed_at
    )
}

/**
 * Extension function to map OfflineVocabularyEntity (SQLDelight) to Vocabulary (Domain Model).
 */
fun OfflineVocabularyEntity.toDomain(): Vocabulary {
    return Vocabulary(
        id = id,
        levelId = level_id.toInt(),
        word = word,
        phonetic = phonetic,
        definition = definition,
        example = example
    )
}

/**
 * Extension function to map SavedVocabularyEntity (SQLDelight) to SavedVocab (Domain Model).
 */
fun SavedVocabularyEntity.toDomain(): com.example.cakapAi.domain.model.SavedVocab {
    return com.example.cakapAi.domain.model.SavedVocab(
        id = id.toInt(),
        sourceLang = source_lang,
        targetLang = target_lang,
        sourceText = source_text,
        translatedText = translated_text,
        createdAt = created_at
    )
}
