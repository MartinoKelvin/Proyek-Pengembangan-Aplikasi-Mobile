package com.example.cakapAi.domain.repository

import com.example.cakapAi.domain.model.SavedVocab
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DictionaryRepositoryTest {

    private lateinit var repository: FakeDictionaryRepository

    @BeforeTest
    fun setup() {
        repository = FakeDictionaryRepository()
    }

    @Test
    fun insertVocabulary_shouldAddData() = runTest {
        val vocab = SavedVocab(
            sourceLang = "English",
            targetLang = "Indonesian",
            sourceText = "Apple",
            translatedText = "Apel"
        )
        
        repository.insertSavedVocab(vocab)
        val items = repository.getAllSavedVocabs().first()
        
        assertEquals(1, items.size)
        assertEquals("Apple", items[0].sourceText)
    }

    @Test
    fun updateVocabulary_shouldChangeData() = runTest {
        val vocab = SavedVocab(
            sourceLang = "English",
            targetLang = "Indonesian",
            sourceText = "Apple",
            translatedText = "Apel"
        )
        repository.insertSavedVocab(vocab)
        val inserted = repository.getAllSavedVocabs().first().first()

        val updatedVocab = inserted.copy(translatedText = "Buah Apel")
        repository.updateSavedVocab(updatedVocab)

        val updatedItem = repository.getVocabById(inserted.id)
        assertNotNull(updatedItem)
        assertEquals("Buah Apel", updatedItem?.translatedText)
    }

    @Test
    fun deleteVocabulary_shouldRemoveData() = runTest {
        val vocab = SavedVocab(
            sourceLang = "English",
            targetLang = "Indonesian",
            sourceText = "Cat",
            translatedText = "Kucing"
        )
        repository.insertSavedVocab(vocab)
        val inserted = repository.getAllSavedVocabs().first().first()
        
        assertEquals(1, repository.getAllSavedVocabs().first().size)

        repository.deleteSavedVocab(inserted.id)
        
        assertEquals(0, repository.getAllSavedVocabs().first().size)
        assertNull(repository.getVocabById(inserted.id))
    }

    @Test
    fun getVocabularyById_shouldReturnCorrectData() = runTest {
        val vocab1 = SavedVocab(sourceLang = "En", targetLang = "Id", sourceText = "Dog", translatedText = "Anjing")
        val vocab2 = SavedVocab(sourceLang = "En", targetLang = "Id", sourceText = "Bird", translatedText = "Burung")
        repository.insertSavedVocab(vocab1)
        repository.insertSavedVocab(vocab2)

        val allItems = repository.getAllSavedVocabs().first()
        val targetId = allItems[1].id

        val found = repository.getVocabById(targetId)
        assertNotNull(found)
        assertEquals("Bird", found?.sourceText)
    }

    @Test
    fun searchVocabulary_shouldReturnMatchingData() = runTest {
        repository.insertSavedVocab(SavedVocab(sourceLang = "En", targetLang = "Id", sourceText = "Book", translatedText = "Buku"))
        repository.insertSavedVocab(SavedVocab(sourceLang = "En", targetLang = "Id", sourceText = "Pencil", translatedText = "Pensil"))
        repository.insertSavedVocab(SavedVocab(sourceLang = "En", targetLang = "Id", sourceText = "Pen", translatedText = "Pulpen"))

        val searchResult = repository.searchVocabs("pen")
        assertEquals(2, searchResult.size) // Pencil and Pen
        assertTrue(searchResult.any { it.sourceText == "Pencil" })
        assertTrue(searchResult.any { it.sourceText == "Pen" })
    }
}
