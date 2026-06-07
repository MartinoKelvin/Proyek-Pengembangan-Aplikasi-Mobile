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
                Kamu adalah penerjemah bahasa yang sangat akurat.
                Tugasmu adalah menerjemahkan teks dari bahasa $sourceLang ke bahasa $targetLang.

                ATURAN BALASAN (SANGAT KETAT):
                1. Tuliskan HANYA hasil terjemahan langsungnya saja.
                2. JANGAN sertakan penjelasan, definisi, alternatif, pengulangan teks sumber, ataupun contoh kalimat.
                3. JANGAN gunakan tanda petik atau format markdown apapun pada hasil terjemahan.
                4. Output harus bersih berupa teks hasil terjemahan saja.

                Teks sumber untuk diterjemahkan: "$text"
            """.trimIndent()
            
            geminiService.generateContent(prompt).onSuccess { result ->
                _translationResult.value = result
            }.onFailure { e ->
                _translationResult.value = "Gagal menerjemahkan: ${e.message ?: "Periksa koneksi internet Anda."}"
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
