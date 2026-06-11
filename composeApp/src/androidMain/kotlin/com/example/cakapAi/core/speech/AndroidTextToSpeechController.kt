package com.example.cakapAi.core.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AndroidTextToSpeechController(context: Context) : TextToSpeechController, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isInitialized = true
            pendingText?.let {
                speak(it)
                pendingText = null
            }
        }
    }

    override fun speak(text: String) {
        if (isInitialized) {
            val locale = detectLocale(text)
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            pendingText = text
        }
    }

    private fun detectLocale(text: String): Locale {
        val cleaned = text.lowercase().replace(Regex("[^a-z\\s]"), " ")
        val words = cleaned.split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (words.isEmpty()) return Locale.US

        val indonesianWords = setOf(
            "yang", "dan", "di", "ke", "dari", "ini", "itu", "adalah", "untuk", "dengan",
            "saya", "kamu", "dia", "mereka", "kita", "kami", "tidak", "bisa", "ada",
            "apa", "siapa", "mengapa", "bagaimana", "kapan", "dimana", "selamat", "pagi",
            "siang", "sore", "malam", "terima", "kasih", "arti", "terjemahkan", "pilih",
            "kalimat", "ungkapan", "kata", "benda", "hewan", "buah", "tata", "bahasa",
            "percakapan", "pengucapan", "arah", "toilet", "tolong", "bantu", "periksa",
            "silakan", "paman", "bibi", "kakek", "nenek", "ayah", "ibu", "teman", "angka",
            "merujuk", "perempuan", "laki", "sangat", "seperti", "sebelum", "sesaat", "benar",
            "salah", "opsi", "pilihan", "soal", "kuis", "latihan", "kamus", "penerjemah"
        )

        val matchCount = words.count { it in indonesianWords }
        return if (matchCount > 0) {
            Locale("id", "ID")
        } else {
            Locale.US
        }
    }

    override fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }
}
