package com.example.cakapAi.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object Map : Route

    @Serializable
    data class Quiz(
        val levelId: Int
    ) : Route

    @Serializable
    data class Result(
        val score: Int,
        val totalQuestion: Int,
        val accuracy: Int,
        val isPassed: Boolean
    ) : Route

    @Serializable
    data object Dictionary : Route

    @Serializable
    data object AITutor : Route
}

interface NavigationActions {
    fun navigateToMap()
    fun navigateToQuiz(levelId: Int)
    fun navigateToResult(
        score: Int,
        totalQuestion: Int,
        accuracy: Int,
        isPassed: Boolean
    )
    fun navigateToDictionary()
    fun navigateToAITutor()
    fun navigateBack()
}