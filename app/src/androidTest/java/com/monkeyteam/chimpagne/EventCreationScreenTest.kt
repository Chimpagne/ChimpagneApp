package com.monkeyteam.chimpagne

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModelFactory
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

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPanels() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    // Move to the Second Panel

    composeTestRule.onNodeWithTag("next_button").performClick()
    // composeTestRule.onNodeWithTag("next_button")
    // composeTestRule.onNodeWithText("More event infos").assertIsDisplayed()

    // Return to the First Panel
    composeTestRule.onNodeWithTag("previous_button").performClick()

    // composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }
  /*
  @Test
  fun testLocationSelector() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions)
    }

    composeTestRule.onNodeWithTag("LocationComponent").assertIsDisplayed()

  }*/

  @Test
  fun testPanel1() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
  }

  @Test
  fun testSecondPanelContent() {
    var tagsLegendS = ""
    var publicLegendS = ""

    // Given
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))

      val context = LocalContext.current
      tagsLegendS = context.getString(R.string.event_creation_screen_tags_legend)
      publicLegendS = context.getString(R.string.event_creation_screen_public_legend)
    }

    // When - Then
    composeTestRule.onNodeWithText(tagsLegendS).assertIsDisplayed()
    composeTestRule.onNodeWithText(publicLegendS).assertIsDisplayed()

    // You can add more detailed tests here for interactions and assertions
  }

  @Test
  fun testMakeEventPublicButtonShowsToast() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))
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
      EventCreationScreen(2, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    // composeTestRule.onNodeWithTag("tag_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("groceries_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_groceries_button").assertIsDisplayed()
  }

  @Test
  fun testInputIntoTextFields() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithTag("add_a_title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
    composeTestRule.onNodeWithTag("previous_button").assertDoesNotExist()
  }

  @Test
  fun testFourthPanel() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    val value = "4"
    composeTestRule.onNodeWithTag("n_parking").assertExists()
    composeTestRule.onNodeWithTag("n_parking").performTextInput(value)

    composeTestRule.onNodeWithText(value).assertIsDisplayed()

    val valueBed = "2"
    composeTestRule.onNodeWithTag("n_beds").assertExists()
    composeTestRule.onNodeWithTag("n_beds").performTextInput(valueBed)

    composeTestRule.onNodeWithText(valueBed).assertIsDisplayed()
    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()

    composeTestRule.onNodeWithTag("create_event_button").performClick()
  }

  @Test
  fun testInvalidPanel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          19, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
  }

  @Test
  fun testUIHelpingFunctions() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithTag("logistics_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("parking_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("beds_title").assertIsDisplayed()
  }
}
