package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentEventCreationScreenTest {
  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.monkeyteam.chimpagne", appContext.packageName)
  }
}

@RunWith(AndroidJUnit4::class)
class EventCreationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  /*
  @Test
  fun TestPanels() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions)
    }

    // Move to the Second Panel
    composeTestRule.onNodeWithText("Next").performClick()
    composeTestRule.onNodeWithText("More event infos").assertIsDisplayed()

    // Return to the First Panel
    composeTestRule.onNodeWithText("Previous").performClick()
    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }


  @Test
  fun testPanel1() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(1, navActions)
    }
    composeTestRule.onNodeWithText("Tags (comma-separated)").assertIsDisplayed()
  }

  @Test
  fun testMakeEventPublicButtonShowsToast() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventCreationScreen(1, navActions)
    }

    // Tags (comma-separated)
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
    composeTestRule.onNodeWithText("Logistics").assertDoesNotExist()
    composeTestRule.onNodeWithText("Parking").assertDoesNotExist()
    composeTestRule.onNodeWithText("Beds").assertDoesNotExist()
    // This will attempt to click the button and create a Toast.
    // Note that testing the actual visibility of a Toast is beyond the scope of Compose UI Tests.
    composeTestRule.onNodeWithText("Make this event public").performClick()
  }

  @Test
  fun testPanel2() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(2, navActions)
    }

    composeTestRule.onNodeWithText("Groceries").assertIsDisplayed()
    composeTestRule.onNodeWithText("Add groceries").assertIsDisplayed()
  }

  @Test
  fun testInputIntoTextFields() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventCreationScreen(0, navActions)
    }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithText("Title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
    composeTestRule.onNodeWithText("Previous").assertDoesNotExist()
  }*/

  @Test
  fun testFourthPanel() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions)
    }

    val value = "4"
    composeTestRule.onNodeWithText("Number of parking places").assertExists()
    composeTestRule.onNodeWithText("Number of parking places").performTextInput(value)

    composeTestRule.onNodeWithText(value).assertIsDisplayed()

    val valueBed = "2"
    composeTestRule.onNodeWithText("Number of beds").assertExists()
    composeTestRule.onNodeWithText("Number of beds").performTextInput(valueBed)

    composeTestRule.onNodeWithText(valueBed).assertIsDisplayed()
    composeTestRule.onNodeWithText("Next").assertDoesNotExist()

    composeTestRule.onNodeWithText("Create Event").performClick()
  }

  @Test
  fun testInvalidPanel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(19, navActions)
    }
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
  }

  @Test
  fun testUIHelpingFunctions() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions)
    }
    composeTestRule.onNodeWithText("Logistics").assertIsDisplayed()
    composeTestRule.onNodeWithText("Parking").assertIsDisplayed()
    composeTestRule.onNodeWithText("Beds").assertIsDisplayed()
  }
}
