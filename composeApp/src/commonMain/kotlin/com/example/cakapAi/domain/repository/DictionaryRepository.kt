package com.example.cakapAi.domain.repository

import com.example.cakapAi.domain.model.SavedVocab
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    fun getAllSavedVocabs(): Flow<List<SavedVocab>>
    suspend fun insertSavedVocab(vocab: SavedVocab)
    suspend fun updateSavedVocab(vocab: SavedVocab)
    suspend fun deleteSavedVocab(id: Int)
}
