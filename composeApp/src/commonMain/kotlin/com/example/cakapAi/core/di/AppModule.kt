package com.example.cakapAi.core.di

import com.example.cakapAi.core.network.HttpClientFactory
import com.example.cakapAi.core.util.DatabaseDriverFactory
import com.example.cakapAi.data.local.NoteDatabase
import com.example.cakapAi.data.local.datastore.DataStoreFactory
import com.example.cakapAi.data.local.datastore.UserPreferences
import com.example.cakapAi.data.local.datastore.create
import com.example.cakapAi.data.remote.api.GeminiService
import com.example.cakapAi.data.repository.AIRepositoryImpl
import com.example.cakapAi.data.repository.LearningRepositoryImpl
import com.example.cakapAi.domain.repository.AIRepository
import com.example.cakapAi.domain.repository.LearningRepository
import com.example.cakapAi.presentation.screens.map.MapViewModel
import com.example.cakapAi.presentation.screens.quiz.QuizViewModel
import com.example.cakapAi.presentation.screens.result.ResultViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

// ==================== NETWORK MODULE ====================

val networkModule = module {
    single { HttpClientFactory.create(enableLogging = true) }
    singleOf(::GeminiService)
}

// ==================== DATABASE MODULE ====================

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NoteDatabase(driverFactory.createDriver())
    }
}

// ==================== PREFERENCES MODULE ====================

val preferencesModule = module {
    single { get<DataStoreFactory>().create() }
    single { UserPreferences(get()) }
}

// ==================== REPOSITORY MODULE ====================

val repositoryModule = module {
    singleOf(::AIRepositoryImpl) bind AIRepository::class
    single<LearningRepository> { LearningRepositoryImpl(get()) }
}

// ==================== VIEWMODEL MODULE ====================

val viewModelModule = module {
    viewModelOf(::MapViewModel)
    viewModel { (levelId: Int) -> QuizViewModel(levelId = levelId, repository = get()) }
    viewModelOf(::ResultViewModel)
}

// ==================== SHARED MODULES ====================

val sharedModules = listOf(
    networkModule,
    databaseModule,
    preferencesModule,
    repositoryModule,
    viewModelModule
)

// ==================== INIT FUNCTION ====================

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(platformModules + sharedModules)
    }
}
