package com.contextphoto

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.java

@RunWith(AndroidJUnit4::class)
class UITestAlbums {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @Test
    fun testShowAddAlbumDialog() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("fabAddAlbum").performClick()
        composeTestRule.onNodeWithText("Создание альбома").assertExists()
    }

    @Test
    fun testShowBottomBar() {
        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasText("Альбомы") and hasClickAction()).assertExists()
        composeTestRule.onNodeWithText("Все фото").assertExists()
    }

    @Test
    fun testShowMainDropdownMenu() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("mainDropdownMenu")
            .assertExists()
            .assertIsDisplayed()
            .assertIsEnabled()
    }

}