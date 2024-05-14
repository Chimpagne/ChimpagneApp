package com.monkeyteam.chimpagne

import android.content.Context
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.ui.components.SocialButton
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
import com.monkeyteam.chimpagne.ui.components.SupportedSocialMedia
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SocialMediaTests {

  @get:Rule val composeTestRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun testSocialButton() {
    composeTestRule.setContent {
      SocialButton(
          imageLogo = R.drawable.discord,
          urlAsString = "https://discord.gg/",
          context = context,
          testTag = "Discord_Button")
    }

    composeTestRule.onNodeWithTag("Discord_Button").assertExists().isDisplayed()
  }

  @Test
  fun AllThreeDisplayed() {
    val mapSocialMedia = SupportedSocialMedia.associateBy { it.platformName }
    val filledInSocialMedia =
        mapSocialMedia.mapValues { it.value.copy(chosenGroupUrl = it.value.platformUrl) }
    composeTestRule.setContent {
      SocialButtonRow(context = context, socialMediaLinks = filledInSocialMedia)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("discord_input").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("whatsapp_input").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("telegram_input").assertExists().isDisplayed()
  }

  @Test
  fun OnlyTelegramDisplayed() {
    val socialMediaMap = SupportedSocialMedia.associateBy { it.platformName }
    val telegramMap =
        mapOf(socialMediaMap["telegram"]!!.platformName to socialMediaMap["telegram"]!!)
    val withValueMap =
        telegramMap.mapValues { it.value.copy(chosenGroupUrl = it.value.platformUrl) }
    composeTestRule.setContent {
      SocialButtonRow(context = context, socialMediaLinks = withValueMap)
    }
    composeTestRule.onNodeWithTag("discord_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("whatsapp_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("telegram_input").assertExists().isDisplayed()
  }

  @Test
  fun NoneDisplayed() {
    composeTestRule.setContent { SocialButtonRow(context = context, socialMediaLinks = emptyMap()) }
    composeTestRule.onNodeWithTag("discord_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("whatsapp_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("telegram_input").assertDoesNotExist()
  }
}
