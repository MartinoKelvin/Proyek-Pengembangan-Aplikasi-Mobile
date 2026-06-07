package com.example.cakapAi.presentation.screens.tutor

import com.example.cakapAi.data.remote.api.GeminiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AITutorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_shouldHaveWelcomeMessage() = runTest {
        val client = HttpClient(MockEngine { respond("") }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val geminiService = GeminiService(client)
        val viewModel = AITutorViewModel(geminiService)

        val history = viewModel.chatHistory.first()
        assertEquals(1, history.size)
        assertFalse(history[0].isUser)
        assertFalse(viewModel.isAITyping.first())
    }

    @Test
    fun sendMessage_blankText_shouldDoNothing() = runTest {
        val client = HttpClient(MockEngine { respond("") }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val geminiService = GeminiService(client)
        val viewModel = AITutorViewModel(geminiService)

        viewModel.sendMessage("   ")
        advanceUntilIdle()

        val history = viewModel.chatHistory.first()
        assertEquals(1, history.size)
    }

    @Test
    fun sendMessage_validText_successResponse_shouldUpdateHistory() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = """{"candidates": [{"content": {"parts": [{"text": "Great grammar!"}]}}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val geminiService = GeminiService(client)
        val viewModel = AITutorViewModel(geminiService)

        viewModel.sendMessage("I goes to school")
        
        // Before completion, user message is added and AI is typing
        assertTrue(viewModel.isAITyping.value)
        assertEquals(2, viewModel.chatHistory.value.size)
        assertTrue(viewModel.chatHistory.value[1].isUser)

        advanceUntilIdle()

        // After completion, AI response is added
        val finalHistory = viewModel.chatHistory.value
        assertEquals(3, finalHistory.size)
        assertFalse(finalHistory[2].isUser)
        assertTrue(finalHistory[2].isFeedback)
        assertEquals("Great grammar!", finalHistory[2].text)
        assertFalse(viewModel.isAITyping.value)
    }

    @Test
    fun sendMessage_apiError_shouldShowErrorMessage() = runTest {
        val mockEngine = MockEngine {
            respondError(HttpStatusCode.InternalServerError)
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val geminiService = GeminiService(client)
        val viewModel = AITutorViewModel(geminiService)

        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        val finalHistory = viewModel.chatHistory.value
        assertEquals(3, finalHistory.size)
        assertFalse(finalHistory[2].isUser)
        assertTrue(finalHistory[2].text.contains("Error"))
        assertFalse(viewModel.isAITyping.value)
    }
}
