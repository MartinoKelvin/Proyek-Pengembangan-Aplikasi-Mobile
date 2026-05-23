package com.example.cakapAi.presentation.screens.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.domain.model.SavedVocab
import com.example.cakapAi.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    val savedVocabs: StateFlow<List<SavedVocab>> = repository.getAllSavedVocabs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addVocab(sourceLang: String, targetLang: String, sourceText: String, translatedText: String) {
        viewModelScope.launch {
            repository.insertSavedVocab(
                SavedVocab(
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                    sourceText = sourceText,
                    translatedText = translatedText
                )
            )
        }
    }

    fun updateVocab(vocab: SavedVocab, newSourceText: String, newTranslatedText: String) {
        viewModelScope.launch {
            repository.updateSavedVocab(
                vocab.copy(
                    sourceText = newSourceText,
                    translatedText = newTranslatedText
                )
            )
        }
    }

    fun deleteVocab(vocab: SavedVocab) {
        viewModelScope.launch {
            repository.deleteSavedVocab(vocab.id)
        }
    }
}
