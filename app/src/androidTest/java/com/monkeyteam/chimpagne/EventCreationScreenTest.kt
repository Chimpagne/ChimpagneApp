package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.ui.EventCreationScreen
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
    composeTestRule.setContent { EventCreationScreen(0) }

    // Move to the Second Panel
    composeTestRule.onNodeWithText("Next").performClick()
    composeTestRule.onNodeWithText("More event infos").assertIsDisplayed()

    // Return to the First Panel
    composeTestRule.onNodeWithText("Previous").performClick()
    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }

  @Test
  fun testMakeEventPublicButtonShowsToast() {
    composeTestRule.setContent { EventCreationScreen(1) }

    composeTestRule.onNodeWithText("Logistics").assertDoesNotExist()
    composeTestRule.onNodeWithText("Parking").assertDoesNotExist()
    composeTestRule.onNodeWithText("Beds").assertDoesNotExist()
    // This will attempt to click the button and create a Toast.
    // Note that testing the actual visibility of a Toast is beyond the scope of Compose UI Tests.
    composeTestRule.onNodeWithText("Make this event public").performClick()
  }

  @Test
  fun testInputIntoTextFields() {
    composeTestRule.setContent { EventCreationScreen(0) }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithText("Title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
  }

  @Test
  fun testFourthPanel() {
    // Now we do this to go the correct screen
    composeTestRule.setContent { EventCreationScreen(4) }

    val value = "4"
    composeTestRule.onNodeWithText("Number of parking spaces").performTextInput(value)

    composeTestRule.onNodeWithText(value).assertIsDisplayed()

    val valueBed = "2"
    composeTestRule.onNodeWithText("Number of beds").performTextInput(valueBed)

    composeTestRule.onNodeWithText(valueBed).assertIsDisplayed()

    composeTestRule.onNodeWithText("Create Event").performClick()
  }

  @Test
  fun testUIHelpingFunctions() {
    composeTestRule.setContent { EventCreationScreen(4) }
    composeTestRule.onNodeWithText("Logistics").assertIsDisplayed()
    composeTestRule.onNodeWithText("Parking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Beds").assertIsDisplayed()
  }
}
