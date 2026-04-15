package com.contextphoto

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITestNavigate {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testOpenLoginPage() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Войти в аккаунт").performClick()
    }

    @Test
    fun testOpenRegisterPage() {
        composeTestRule.waitForIdle()
        testOpenLoginPage()
        composeTestRule.onNodeWithText("Создать аккаунт").performClick()
    }

    @Test
    fun testOpenPolicyConfidence() {
        composeTestRule.waitForIdle()
        testOpenRegisterPage()
        composeTestRule.onNodeWithText("Согласие с политикой конфиденциальности").performClick()
        composeTestRule.onNodeWithText("Политика конфиденциальности").isDisplayed()
    }

    @Test
    fun testRedirectToLoginPage() {
        composeTestRule.waitForIdle()
        testOpenRegisterPage()
        composeTestRule.onNodeWithText("Уже есть аккаунт? Войдите").performClick()
    }


}