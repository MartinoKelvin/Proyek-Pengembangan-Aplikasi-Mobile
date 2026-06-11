package com.example.cakapAi.data.repository

import com.example.cakapAi.data.remote.api.GeminiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PracticeRepositoryImplTest {

    private val mockEngine = MockEngine { request ->
        respond(
            content = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "[\n  {\n    \"id\": \"1\",\n    \"levelId\": 1,\n    \"type\": \"MULTIPLE_CHOICE\",\n    \"instruction\": \"Test\",\n    \"prompt\": \"Test Prompt\",\n    \"options\": [\"A\"],\n    \"correctAnswer\": \"A\",\n    \"explanation\": \"Exp\"\n  }\n]"
                          }
                        ]
                      }
                    }
                  ]
                }
            """.trimIndent(),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }

    private val httpClient = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val fakeGeminiService = GeminiService(httpClient)

    @Test
    fun testGeneratePracticeQuestions() = runTest {
        val repo = PracticeRepositoryImpl(fakeGeminiService)
        val result = repo.generatePracticeQuestions(1, "Title", "Type")
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(1, list.size)
        assertEquals("1", list[0].id)
    }

    @Test
    fun testGetOfflineQuestions() = runTest {
        val repo = PracticeRepositoryImpl(fakeGeminiService)
        
        for (lvl in 1..20) {
            val questions = repo.getOfflineQuestions(lvl)
            assertEquals(10, questions.size, "Level $lvl should have exactly 10 questions")
        }
        
        val levelOther = repo.getOfflineQuestions(99)
        assertEquals(10, levelOther.size, "Fallback level should have exactly 10 questions")
    }
}
