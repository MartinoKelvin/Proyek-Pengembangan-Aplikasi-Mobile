package com.example.cakapAi.data.repository

import com.example.cakapAi.data.remote.api.GeminiService
import com.example.cakapAi.domain.model.PracticeQuestion
import com.example.cakapAi.domain.model.PracticeQuestionType
import com.example.cakapAi.domain.repository.PracticeRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class PracticeRepositoryImpl(
    private val geminiService: GeminiService
) : PracticeRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generatePracticeQuestions(
        levelId: Int,
        levelTitle: String,
        levelType: String
    ): Result<List<PracticeQuestion>> = runCatching {
        val prompt = """
            Kamu adalah guru bahasa Inggris profesional. Buat 5 soal latihan bahasa Inggris berdasarkan data berikut:
            - Level ID: $levelId
            - Tema Level: $levelTitle
            - Fokus Latihan: $levelType

            ATURAN WAJIB:
            1. Berikan campuran dari 3 tipe soal: "MULTIPLE_CHOICE", "FILL_BLANK", dan "SPEAKING".
            2. Untuk soal "FILL_BLANK", WAJIB berikan tepat 3 kata pengecoh beserta kata yang benar di dalam array "options".
            3. Untuk soal "SPEAKING", "options" boleh kosong [], namun "prompt" harus berisi kalimat yang harus diucapkan user.
            4. "explanation" harus berisi penjelasan singkat dalam bahasa Indonesia mengapa jawaban tersebut benar.
            5. JAWABAN HANYA BOLEH BERUPA JSON ARRAY murni. JANGAN gunakan tag markdown seperti ```json atau teks pembuka/penutup lainnya.

            Format JSON yang diharapkan:
            [
              {
                "id": "unik-id",
                "levelId": $levelId,
                "type": "MULTIPLE_CHOICE",
                "instruction": "Pilih terjemahan yang tepat",
                "prompt": "What is the meaning of 'book'?",
                "options": ["Buku", "Meja", "Kursi", "Pintu"],
                "correctAnswer": "Buku",
                "explanation": "'Book' adalah kata benda dalam bahasa Inggris yang berarti 'Buku'."
              }
            ]
        """.trimIndent()

        val response = geminiService.generateContent(prompt).getOrThrow()
        
        // Clean markdown backticks if any
        val cleanedJson = response.replace("```json", "").replace("```", "").trim()
        
        json.decodeFromString<List<PracticeQuestion>>(cleanedJson)
    }

    override suspend fun getOfflineFallbackQuestions(levelId: Int): List<PracticeQuestion> {
        return listOf(
            PracticeQuestion(
                id = "fallback-1",
                levelId = levelId,
                type = PracticeQuestionType.MULTIPLE_CHOICE,
                instruction = "Pilih jawaban yang benar",
                prompt = "What is the meaning of 'apple'?",
                options = listOf("Apel", "Buku", "Mobil", "Rumah"),
                correctAnswer = "Apel",
                explanation = "Apple berarti apel."
            ),
            PracticeQuestion(
                id = "fallback-2",
                levelId = levelId,
                type = PracticeQuestionType.FILL_BLANK,
                instruction = "Isi bagian kosong",
                prompt = "I ___ a student.",
                options = listOf("am", "is", "are"),
                correctAnswer = "am",
                explanation = "Kalimat yang benar adalah I am a student."
            ),
            PracticeQuestion(
                id = "fallback-3",
                levelId = levelId,
                type = PracticeQuestionType.SPEAKING,
                instruction = "Ucapkan kalimat berikut",
                prompt = "Good morning, how are you?",
                correctAnswer = "Good morning, how are you?",
                explanation = "Latihan pengucapan sapaan dasar."
            ),
            PracticeQuestion(
                id = "fallback-4",
                levelId = levelId,
                type = PracticeQuestionType.MULTIPLE_CHOICE,
                instruction = "Pilih terjemahan yang tepat",
                prompt = "Saya suka membaca buku",
                options = listOf("I like reading books", "I like play games", "I am a student", "She reads a book"),
                correctAnswer = "I like reading books",
                explanation = "I like reading books berarti saya suka membaca buku."
            ),
            PracticeQuestion(
                id = "fallback-5",
                levelId = levelId,
                type = PracticeQuestionType.FILL_BLANK,
                instruction = "Lengkapi kalimat ini",
                prompt = "She ___ from Indonesia.",
                options = listOf("is", "am", "are"),
                correctAnswer = "is",
                explanation = "Menggunakan to be 'is' untuk subjek She."
            )
        )
    }
}
