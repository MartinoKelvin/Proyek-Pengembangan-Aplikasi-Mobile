package com.example.cakapAi.domain.repository

import com.example.cakapAi.domain.model.SavedVocab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeDictionaryRepository : DictionaryRepository {
    private val vocabs = MutableStateFlow<List<SavedVocab>>(emptyList())
    private var nextId = 1

    override fun getAllSavedVocabs(): Flow<List<SavedVocab>> {
        return vocabs
    }

    override suspend fun insertSavedVocab(vocab: SavedVocab) {
        val newVocab = vocab.copy(id = nextId++)
        vocabs.update { it + newVocab }
    }

    override suspend fun updateSavedVocab(vocab: SavedVocab) {
        vocabs.update { currentList ->
            currentList.map { if (it.id == vocab.id) vocab else it }
        }
    }

    override suspend fun deleteSavedVocab(id: Int) {
        vocabs.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }

    // Helper method for testing getById
    fun getVocabById(id: Int): SavedVocab? {
        return vocabs.value.find { it.id == id }
    }

    // Helper method for testing search
    fun searchVocabs(keyword: String): List<SavedVocab> {
        return vocabs.value.filter {
            it.sourceText.contains(keyword, ignoreCase = true) ||
            it.translatedText.contains(keyword, ignoreCase = true)
        }
    }
}
