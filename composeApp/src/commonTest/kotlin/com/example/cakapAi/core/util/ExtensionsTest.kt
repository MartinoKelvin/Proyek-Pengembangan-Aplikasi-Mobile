package com.example.cakapAi.core.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExtensionsTest {

    @Test
    fun stringTruncate_whenLongerThanMaxLength_shouldTruncateWithEllipsis() {
        val original = "This is a very long string"
        val truncated = original.truncate(10)
        assertEquals("This is...", truncated)
    }

    @Test
    fun stringTruncate_whenShorterThanMaxLength_shouldNotTruncate() {
        val original = "Short"
        val truncated = original.truncate(10)
        assertEquals("Short", truncated)
    }

    @Test
    fun stringCapitalizeFirst_shouldCapitalizeFirstLetter() {
        assertEquals("Hello", "hello".capitalizeFirst())
        assertEquals("Hello", "Hello".capitalizeFirst())
        assertEquals("", "".capitalizeFirst())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun retryWithBackoff_whenSucceedsOnFirstTry_shouldReturnResult() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3) {
            attempts++
            "Success"
        }
        assertEquals("Success", result)
        assertEquals(1, attempts)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun retryWithBackoff_whenFailsFirstButSucceedsLater_shouldReturnResult() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3) {
            attempts++
            if (attempts < 2) throw Exception("Fail")
            "Success"
        }
        assertEquals("Success", result)
        assertEquals(2, attempts)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun retryWithBackoff_whenFailsAllTimes_shouldThrowException() = runTest {
        var attempts = 0
        assertFailsWith<Exception>("Fail") {
            retryWithBackoff(times = 3) {
                attempts++
                throw Exception("Fail")
            }
        }
        assertEquals(3, attempts)
    }

    @Test
    fun resultMapSuccess_shouldMapValueOnSuccess() {
        val result = Result.success(10)
        val mapped = result.mapSuccess { it * 2 }
        assertEquals(Result.success(20), mapped)
    }

    @Test
    fun resultHandle_shouldInvokeOnSuccess() {
        val result = Result.success("OK")
        var successVal = ""
        result.handle(
            onSuccess = { successVal = it },
            onFailure = { }
        )
        assertEquals("OK", successVal)
    }

    @Test
    fun resultHandle_shouldInvokeOnFailure() {
        val result = Result.failure<String>(Exception("Error"))
        var errorMsg = ""
        result.handle(
            onSuccess = { },
            onFailure = { errorMsg = it.message ?: "" }
        )
        assertEquals("Error", errorMsg)
    }
}
