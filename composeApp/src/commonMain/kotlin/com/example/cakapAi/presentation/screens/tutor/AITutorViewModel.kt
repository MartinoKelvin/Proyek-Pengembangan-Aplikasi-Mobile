package com.example.cakapAi.presentation.screens.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cakapAi.data.remote.api.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val isFeedback: Boolean = false
)

class AITutorViewModel(
    private val geminiService: GeminiService
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "Halo! Aku AI Tutor kamu. Coba ketik kalimat dalam bahasa Inggris dan aku akan memberikan feedback mengenai grammar-mu!"
            )
        )
    )
    val chatHistory = _chatHistory.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    val isAITyping = _isAITyping.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message
        val updatedHistory = _chatHistory.value.toMutableList()
        updatedHistory.add(ChatMessage(isUser = true, text = text))
        _chatHistory.value = updatedHistory

        _isAITyping.value = true

        viewModelScope.launch {
            val prompt = """
                Kamu adalah 'CakapAI Tutor', asisten belajar bahasa Inggris pribadi yang sangat ramah, suportif, dan gaul. 
                Tugas utamamu adalah:
                1. Menganalisa setiap kalimat bahasa Inggris yang diketikkan pengguna.
                2. Jika ada kesalahan grammar atau pemilihan kata (vocabulary), berikan koreksi yang sopan, jelaskan kesalahannya dalam bahasa Indonesia, lalu berikan contoh kalimat yang lebih natural (native-like).
                3. Jika kalimatnya sudah sempurna, berikan pujian dan lanjutkan percakapan dengan bertanya kembali dalam bahasa Inggris untuk memancing pengguna terus berlatih.
                4. Jangan memberikan jawaban yang terlalu panjang, buatlah seakan-akan ini adalah chat WhatsApp (singkat, padat, dan gunakan emoji secukupnya).

                Pesan dari pengguna: "$text"
            """.trimIndent()

            geminiService.generateContent(prompt).onSuccess { response ->
                val newHistory = _chatHistory.value.toMutableList()
                newHistory.add(
                    ChatMessage(
                        isUser = false,
                        text = response,
                        isFeedback = true
                    )
                )
                _chatHistory.value = newHistory
            }.onFailure { error ->
                val newHistory = _chatHistory.value.toMutableList()
                newHistory.add(
                    ChatMessage(
                        isUser = false,
                        text = "Maaf, aku sedang tidak bisa merespons saat ini. Periksa koneksi internetmu ya! Error: ${error.message}",
                        isFeedback = true
                    )
                )
                _chatHistory.value = newHistory
            }
            
            _isAITyping.value = false
        }
    }
}
