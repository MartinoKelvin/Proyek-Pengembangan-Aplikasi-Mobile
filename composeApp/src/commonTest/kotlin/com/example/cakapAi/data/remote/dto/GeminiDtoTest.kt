package com.example.cakapAi.data.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeminiDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testGeminiRequestSerialization() {
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart("Hello API")),
                    role = "user"
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.5),
            safetySettings = listOf(
                SafetySetting(category = "HARM_CATEGORY_HATE_SPEECH", threshold = "BLOCK_NONE")
            )
        )
        
        val jsonString = json.encodeToString(request)
        assertTrue(jsonString.contains("Hello API"))
        assertTrue(jsonString.contains("HARM_CATEGORY_HATE_SPEECH"))
        
        val decoded = json.decodeFromString<GeminiRequest>(jsonString)
        assertEquals(request, decoded)
        assertEquals(request.hashCode(), decoded.hashCode())
        assertEquals(request.toString(), decoded.toString())
    }

    @Test
    fun testGeminiResponseHelperExtensions_Success() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(parts = listOf(GeminiPart("This is a response"))),
                    finishReason = "STOP"
                )
            )
        )
        
        assertEquals("This is a response", response.getTextContent())
        assertFalse(response.isBlocked())
        assertNull(response.getErrorMessage())
        
        val copied = response.copy()
        assertEquals(response, copied)
    }

    @Test
    fun testGeminiResponseHelperExtensions_Blocked() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(
                blockReason = "SAFETY",
                safetyRatings = listOf(SafetyRating("HATE", "HIGH"))
            )
        )
        
        assertNull(response.getTextContent())
        assertTrue(response.isBlocked())
        assertEquals("Konten diblokir: SAFETY", response.getErrorMessage())
    }

    @Test
    fun testGeminiResponseHelperExtensions_Error() {
        val response = GeminiResponse(
            error = GeminiError(code = 400, message = "Bad Request", status = "FAILED")
        )
        
        assertNull(response.getTextContent())
        assertFalse(response.isBlocked())
        assertEquals("Bad Request", response.getErrorMessage())
    }
    
    @Test
    fun testDataClassMethods() {
        val part = GeminiPart("text")
        val part2 = part.copy(text = "text2")
        assertFalse(part == part2)
        
        val content = GeminiContent(listOf(part))
        val config = GenerationConfig()
        val config2 = GenerationConfig(temperature = 0.1, maxOutputTokens = 10, topP = 0.1, topK = 1)
        val safety = SafetySetting("CAT", "HIGH")
        val req = GeminiRequest(listOf(content))
        
        assertNotNull(config.toString())
        assertNotNull(safety.hashCode())
        assertNotNull(req.copy())
    }
}
