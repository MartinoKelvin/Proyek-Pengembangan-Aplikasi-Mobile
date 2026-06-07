package com.example.cakapAi.data.local

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LearningMapperTest {

    @Test
    fun levelProgressEntityToDomain_shouldMapCorrectly() {
        val entity = LevelProgressEntity(
            id = 1L,
            title = "Level 1",
            subtitle = "Dasar",
            level_type = "LISTENING",
            is_unlocked = 1L,
            is_completed = 0L,
            high_score = 80L,
            updated_at = 123456L
        )

        val domain = entity.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Level 1", domain.title)
        assertEquals("Dasar", domain.subtitle)
        assertEquals("LISTENING", domain.levelType)
        assertTrue(domain.isUnlocked)
        assertFalse(domain.isCompleted)
        assertEquals(80, domain.highScore)
        assertEquals(123456L, domain.updatedAt)
    }

    @Test
    fun quizHistoryEntityToDomain_shouldMapCorrectly() {
        val entity = QuizHistoryEntity(
            id = 10L,
            level_id = 2L,
            score = 100L,
            accuracy = 95.0,
            completed_at = 99999L
        )

        val domain = entity.toDomain()

        assertEquals(10L, domain.id)
        assertEquals(2, domain.levelId)
        assertEquals(100, domain.score)
        assertEquals(95.0, domain.accuracy)
        assertEquals(99999L, domain.completedAt)
    }

    @Test
    fun offlineVocabularyEntityToDomain_shouldMapCorrectly() {
        val entity = OfflineVocabularyEntity(
            id = 5L,
            level_id = 1L,
            word = "Apple",
            phonetic = "/ˈæp(ə)l/",
            definition = "Apel",
            example = "I eat an apple"
        )

        val domain = entity.toDomain()

        assertEquals(5L, domain.id)
        assertEquals(1, domain.levelId)
        assertEquals("Apple", domain.word)
        assertEquals("/ˈæp(ə)l/", domain.phonetic)
        assertEquals("Apel", domain.definition)
        assertEquals("I eat an apple", domain.example)
    }

    @Test
    fun savedVocabularyEntityToDomain_shouldMapCorrectly() {
        val entity = SavedVocabularyEntity(
            id = 3L,
            source_lang = "English",
            target_lang = "Indonesian",
            source_text = "Hello",
            translated_text = "Halo",
            created_at = 123L
        )

        val domain = entity.toDomain()

        assertEquals(3, domain.id)
        assertEquals("English", domain.sourceLang)
        assertEquals("Indonesian", domain.targetLang)
        assertEquals("Hello", domain.sourceText)
        assertEquals("Halo", domain.translatedText)
        assertEquals(123L, domain.createdAt)
    }
}
