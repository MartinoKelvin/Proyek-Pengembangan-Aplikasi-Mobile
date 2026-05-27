package com.example.cakapAi.presentation.screens.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.domain.model.SavedVocab
import com.example.cakapAi.domain.repository.DictionaryRepository
import com.example.cakapAi.data.remote.api.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _translationResult = MutableStateFlow<String>("")
    val translationResult = _translationResult.asStateFlow()

    private val _isTranslating = MutableStateFlow<Boolean>(false)
    val isTranslating = _isTranslating.asStateFlow()

    fun translate(sourceLang: String, targetLang: String, text: String) {
        if (text.isBlank()) {
            _translationResult.value = ""
            return
        }
        _isTranslating.value = true
        viewModelScope.launch {
            val prompt = """
                Kamu adalah Kamus dan Translator Bahasa tingkat lanjut.
                Terjemahkan teks berikut dari bahasa $sourceLang ke bahasa $targetLang.
                
                Teks: "$text"
                
                ATURAN BALASAN:
                1. Berikan terjemahan langsungnya dengan jelas.
                2. Jika teks tersebut adalah satu kata tunggal atau frasa idiom, berikan penjelasan singkat maknanya.
                3. Berikan 1 contoh penggunaan kalimat yang natural menggunakan kata/teks tersebut beserta artinya.
                4. Jangan bertele-tele, format balasanmu agar rapi dan mudah dibaca.
            """.trimIndent()
            
            geminiService.generateContent(prompt).onSuccess { result ->
                _translationResult.value = result
            }.onFailure { 
                _translationResult.value = "Gagal menerjemahkan. Periksa koneksi internet Anda."
            }
            _isTranslating.value = false
        }
    }

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
