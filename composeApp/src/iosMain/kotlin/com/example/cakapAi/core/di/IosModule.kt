package com.example.cakapAi.core.di

import com.example.cakapAi.core.util.DatabaseDriverFactory
import com.example.cakapAi.data.local.datastore.DataStoreFactory
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 *
 * Menyediakan dependencies platform yang dipakai di shared modules.
 */
val iosModule = module {
    single { DatabaseDriverFactory() }
    single { DataStoreFactory() }
    single<com.example.cakapAi.core.speech.SpeechRecognizerController> { com.example.cakapAi.core.speech.IosSpeechRecognizerController() }
    single<com.example.cakapAi.core.speech.TextToSpeechController> { com.example.cakapAi.core.speech.IosTextToSpeechController() }
    single<com.example.cakapAi.core.speech.AudioFeedbackController> { com.example.cakapAi.core.speech.IosAudioFeedbackController() }
}

/** Helper untuk dipanggil dari Swift code. */
fun initKoinIOS() {
    initKoin(platformModules = listOf(iosModule))
}
