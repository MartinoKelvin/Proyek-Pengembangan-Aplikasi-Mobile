package com.example.cakapAi.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.cakapAi.data.local.NoteDatabase
import com.example.cakapAi.data.local.toDomain
import com.example.cakapAi.domain.model.SavedVocab
import com.example.cakapAi.domain.repository.DictionaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class DictionaryRepositoryImpl(
    private val database: NoteDatabase
) : DictionaryRepository {

    private val queries = database.cakapAiQueries

    override fun getAllSavedVocabs(): Flow<List<SavedVocab>> {
        return queries.getAllSavedVocabs()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertSavedVocab(vocab: SavedVocab) {
        withContext(Dispatchers.IO) {
            queries.insertSavedVocab(
                source_lang = vocab.sourceLang,
                target_lang = vocab.targetLang,
                source_text = vocab.sourceText,
                translated_text = vocab.translatedText,
                created_at = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun updateSavedVocab(vocab: SavedVocab) {
        withContext(Dispatchers.IO) {
            queries.updateSavedVocab(
                source_text = vocab.sourceText,
                translated_text = vocab.translatedText,
                id = vocab.id.toLong()
            )
        }
    }

    override suspend fun deleteSavedVocab(id: Int) {
        withContext(Dispatchers.IO) {
            queries.deleteSavedVocab(id = id.toLong())
        }
    }
}
