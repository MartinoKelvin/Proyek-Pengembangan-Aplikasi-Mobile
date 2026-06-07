package com.example.cakapAi.data.repository

import com.example.cakapAi.data.remote.api.GeminiService
import com.example.cakapAi.domain.repository.WritingStyle
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

class AIRepositoryImplTest {

    private val mockEngine = MockEngine { request ->
        respond(
            content = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "1. Idea one\n2. Idea two"
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

    private val fakeService = GeminiService(httpClient)

    @Test
    fun testSummarize() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        val res = repo.summarize("Long text")
        assertTrue(res.isSuccess)
    }

    @Test
    fun testGenerateIdeas() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        val res = repo.generateIdeas("topic")
        val list = res.getOrThrow()
        assertEquals(2, list.size)
        assertEquals("Idea one", list[0])
    }

    @Test
    fun testImproveWriting() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        assertTrue(repo.improveWriting("text", WritingStyle.FORMAL).isSuccess)
        assertTrue(repo.improveWriting("text", WritingStyle.CASUAL).isSuccess)
        assertTrue(repo.improveWriting("text", WritingStyle.ACADEMIC).isSuccess)
        assertTrue(repo.improveWriting("text", WritingStyle.CREATIVE).isSuccess)
        assertTrue(repo.improveWriting("text", WritingStyle.NEUTRAL).isSuccess)
    }

    @Test
    fun testTranslate() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        assertTrue(repo.translate("Hello", "id").isSuccess)
    }

    @Test
    fun testChat() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        assertTrue(repo.chat("Hello").isSuccess)
    }

    @Test
    fun testSuggestTitle() = runTest {
        val repo = AIRepositoryImpl(fakeService)
        assertTrue(repo.suggestTitle("Content").isSuccess)
    }
}
