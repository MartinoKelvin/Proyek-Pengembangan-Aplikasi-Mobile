package com.example.cakapAi.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

import kotlinx.datetime.Clock

class DomainModelTest {

    @Test
    fun testLevelProgress() {
        val level1 = LevelProgress(1, "A", "B", "LISTENING", false, false, 0, 0L)
        val level2 = level1.copy(isUnlocked = true)
        
        assertEquals(1, level1.id)
        assertFalse(level1.isUnlocked)
        assertTrue(level2.isUnlocked)
        assertNotEquals(level1, level2)
        assertEquals(level1.hashCode(), level1.copy().hashCode())
        assertEquals(level1.toString(), level1.copy().toString())
    }

    @Test
    fun testNote() {
        val note = Note(id = 1L, title = "Title", content = "Content", category = NoteCategory.GENERAL, color = NoteColor.DEFAULT, isPinned = false, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
        val noteCopy = note.copy()
        
        assertEquals(note, noteCopy)
        assertEquals(note.hashCode(), noteCopy.hashCode())
        assertEquals(note.toString(), noteCopy.toString())
        assertNotEquals(note, note.copy(id = 2L))
    }

    @Test
    fun testPracticeQuestion() {
        val q1 = PracticeQuestion("1", 1, PracticeQuestionType.MULTIPLE_CHOICE, "inst", "prompt", listOf("A", "B"), "A", "exp")
        val q2 = q1.copy(correctAnswer = "B")
        
        assertEquals(q1, q1.copy())
        assertEquals(q1.hashCode(), q1.copy().hashCode())
        assertNotEquals(q1, q2)
        assertEquals("1", q1.id)
    }

    @Test
    fun testQuizHistory() {
        val qh = QuizHistory(1, 1, 100, 0.9, 1000L)
        assertEquals(qh, qh.copy())
        assertNotEquals(qh, qh.copy(id = 2))
    }

    @Test
    fun testSavedVocab() {
        val sv = SavedVocab(1, "en", "id", "hello", "halo", 0L)
        assertEquals(sv, sv.copy())
        assertNotEquals(sv, sv.copy(id = 2))
    }

    @Test
    fun testVocabulary() {
        val v = Vocabulary(1, 1, "word", "ph", "def", "ex")
        assertEquals(v, v.copy())
        assertNotEquals(v, v.copy(id = 2))
    }
    
    // Helper to avoid importing
    private fun assertTrue(value: Boolean) {
        assertEquals(true, value)
    }
}
