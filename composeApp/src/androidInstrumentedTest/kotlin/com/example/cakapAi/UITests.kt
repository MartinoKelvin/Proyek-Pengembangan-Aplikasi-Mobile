package com.example.cakapAi

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import org.junit.Rule
import org.junit.Test

class UITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_shouldDisplayMainContent() {
        composeTestRule.setContent {
            App()
        }
        
        // Wait for Splash screen to disappear (Splash screen delay is 1800ms)
        // We can just use waitUntil to wait for HomeScreen
        composeTestRule.waitUntil(timeoutMillis = 4000) {
            composeTestRule.onAllNodesWithTag("HomeScreen").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify HomeScreen is displayed
        composeTestRule.onNodeWithTag("HomeScreen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Peta Perjalanan Belajar").assertIsDisplayed()
    }

    @Test
    fun searchVocabulary_shouldDisplayMatchingResult() {
        composeTestRule.setContent {
            App()
        }

        // Wait for Splash screen to disappear
        composeTestRule.waitUntil(timeoutMillis = 4000) {
            composeTestRule.onAllNodesWithTag("HomeScreen").fetchSemanticsNodes().isNotEmpty()
        }

        // Navigate to Dictionary using Bottom Navigation Bar
        composeTestRule.onNodeWithContentDescription("Dictionary").performClick()

        // Wait for Dictionary Screen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("SearchField").fetchSemanticsNodes().isNotEmpty()
        }

        // Type keyword
        composeTestRule.onNodeWithTag("SearchField").performTextInput("Apple")

        // Assert search field is displayed
        composeTestRule.onNodeWithTag("SearchField").assertIsDisplayed()
    }

    @Test
    fun profileButton_click_shouldNavigateToProfileScreen() {
        composeTestRule.setContent {
            App()
        }

        composeTestRule.waitUntil(timeoutMillis = 4000) {
            composeTestRule.onAllNodesWithTag("HomeScreen").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("ProfileButton").performClick()

        // Wait for ProfileScreen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("ProfileScreen").fetchSemanticsNodes().isNotEmpty()
        }
        
        composeTestRule.onNodeWithTag("ProfileScreen").assertIsDisplayed()
        
        // Click Settings
        composeTestRule.onNodeWithTag("SettingsButton").performClick()
        
        // Verify Settings Screen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithTag("SettingsScreen").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("SettingsScreen").assertIsDisplayed()
    }
}
