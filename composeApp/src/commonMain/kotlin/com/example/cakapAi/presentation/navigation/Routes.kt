package com.example.cakapAi.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Splash : Route

    @Serializable
    data object Map : Route

    @Serializable
    data class Quiz(
        val levelId: Int
    ) : Route

    @Serializable
    data class Result(
        val levelId: Int,
        val score: Int,
        val totalQuestion: Int,
        val accuracy: Int,
        val isPassed: Boolean
    ) : Route

    @Serializable
    data object Dictionary : Route

    @Serializable
    data object AITutor : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data object Settings : Route
}

interface NavigationActions {
    fun navigateToMap()
    fun navigateToQuiz(levelId: Int)
    fun navigateToResult(
        levelId: Int,
        score: Int,
        totalQuestion: Int,
        accuracy: Int,
        isPassed: Boolean
    )
    fun navigateToDictionary()
    fun navigateToAITutor()
    fun navigateToProfile()
    fun navigateToSettings()
    fun navigateBack()
}