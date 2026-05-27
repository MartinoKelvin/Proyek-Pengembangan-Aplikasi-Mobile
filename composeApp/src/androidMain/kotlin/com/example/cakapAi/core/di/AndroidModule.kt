package com.example.cakapAi.core.di

import com.example.cakapAi.core.util.DatabaseDriverFactory
import com.example.cakapAi.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory: untuk SQLDelight driver
 * - DataStoreFactory     : untuk lokasi file preferences
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single<com.example.cakapAi.core.speech.SpeechRecognizerController> { com.example.cakapAi.core.speech.AndroidSpeechRecognizerController(androidContext()) }
    single<com.example.cakapAi.core.speech.TextToSpeechController> { com.example.cakapAi.core.speech.AndroidTextToSpeechController(androidContext()) }
    single<com.example.cakapAi.core.speech.AudioFeedbackController> { com.example.cakapAi.core.speech.AndroidAudioFeedbackController(androidContext()) }
}
