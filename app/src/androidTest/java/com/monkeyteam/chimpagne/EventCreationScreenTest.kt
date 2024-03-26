package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.FirstPanel
import com.monkeyteam.chimpagne.ui.FourthPanel
import com.monkeyteam.chimpagne.ui.SecondPanel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentEventCreationScreenTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.monkeyteam.chimpagne", appContext.packageName)
  }
}

@RunWith(AndroidJUnit4::class)
class EventCreationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPanels() {
    // Start on the correct screen
    composeTestRule.setContent { EventCreationScreen() }

    // Move to the Second Panel
    composeTestRule.onNodeWithText("Next").performClick()
    composeTestRule.onNodeWithText("More event infos").assertIsDisplayed()

    // Return to the First Panel
    composeTestRule.onNodeWithText("Previous").performClick()
    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }

  @Test
  fun testMakeEventPublicButtonShowsToast() {
    composeTestRule.setContent { SecondPanel() }

    // This will attempt to click the button and create a Toast.
    // Note that testing the actual visibility of a Toast is beyond the scope of Compose UI Tests.
    composeTestRule.onNodeWithText("Make this event public").performClick()
  }

  @Test
  fun testInputIntoTextFields() {
    composeTestRule.setContent { FirstPanel() }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithText("Title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
  }

  @Test
  fun testFourthPanel() {
    composeTestRule.setContent { FourthPanel() }

    val value = "4"
    composeTestRule.onNodeWithText("Number of parking spaces").performTextInput(value)

    composeTestRule.onNodeWithText(value).assertIsDisplayed()

    val valueBed = "2"
    composeTestRule.onNodeWithText("Number of beds").performTextInput(valueBed)

    composeTestRule.onNodeWithText(valueBed).assertIsDisplayed()
  }
}
