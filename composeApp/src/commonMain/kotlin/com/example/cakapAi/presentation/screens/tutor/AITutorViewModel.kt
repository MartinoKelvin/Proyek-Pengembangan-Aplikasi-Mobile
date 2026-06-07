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
            val historyContext = _chatHistory.value.takeLast(10).dropLast(1).joinToString("\n") { msg ->
                if (msg.isUser) "Pengguna: ${msg.text}" else "Tutor: ${msg.text}"
            }

            val prompt = """
                Kamu adalah 'CakapAI Tutor', asisten belajar bahasa Inggris pribadi yang ramah, sopan, dan suportif.
                Tugas utamamu adalah mendampingi percakapan pengguna dalam bahasa Inggris sekaligus membantu mengoreksi grammar-nya.

                ATURAN FORMAT RESPONS (SANGAT PENTING):
                1. JANGAN gunakan markdown format seperti tanda bintang (** atau *), tanda petik dua yang berlebihan, hashtag, list bullet points, atau modifikasi teks lainnya. Wajib tulis dalam teks biasa (plain text).
                2. Batasi penggunaan emoji. Gunakan maksimal 1 emoji saja per respons, atau tidak sama sekali jika tidak terlalu diperlukan.
                3. Jawab secara ringkas, padat, dan ramah seperti chat personal. Jangan menulis artikel/paragraf yang terlalu panjang, namun pastikan penjelasan tetap tuntas, jelas, dan natural.

                TUGAS UTAMA PERCAKAPAN:
                1. Analisis kalimat terbaru dari pengguna.
                2. Jika ada kesalahan tata bahasa (grammar) atau kosakata (vocabulary):
                   - Berikan penjelasan koreksi yang ramah dalam bahasa Indonesia secara tuntas namun tetap ringkas.
                   - Berikan contoh-contoh kalimat perbaikan yang lebih alami (native-like) agar pengguna mudah memahaminya.
                3. Jika kalimat pengguna sudah benar:
                   - Berikan apresiasi/pujian singkat.
                   - Balas percakapan dan berikan 1 pertanyaan singkat dalam bahasa Inggris agar percakapan terus mengalir.

                Riwayat percakapan sebelumnya:
                ${if (historyContext.isNotEmpty()) historyContext else "(Belum ada riwayat)"}

                Pesan terbaru dari pengguna: "$text"
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
