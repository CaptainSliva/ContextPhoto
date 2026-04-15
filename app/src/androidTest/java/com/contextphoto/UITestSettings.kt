package com.contextphoto

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.font.FontVariation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.contextphoto.data.navigation.Destination
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(AndroidJUnit4::class)
class UITestSettings {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testOpenSettings() {
        composeTestRule.waitForIdle()
//        composeTestRule.waitUntil(timeoutMillis = 5000) {
//            composeTestRule.onAllNodesWithText("Настройки").fetchSemanticsNodes().isNotEmpty()
//        }
        composeTestRule.onNodeWithText("Настройки").isDisplayed()
    }

    @Test
    fun testToastNoTokenExportFirebase() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Экспорт в Firebase").performClick()
        composeTestRule.onNodeWithText("Не совершен вход в профиль").isDisplayed()
    }

    @Test
    fun testToastNoTokenImportFirebase() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Импорт из Firebase").performClick()
        composeTestRule.onNodeWithText("Не совершен вход в профиль").isDisplayed()
    }



}