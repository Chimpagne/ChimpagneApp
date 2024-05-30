package com.monkeyteam.chimpagne.newtests.ui.event

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.components.SupportedSocialMedia
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentEditEventScreenTest {
  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.monkeyteam.chimpagne", appContext.packageName)
  }
}

@RunWith(AndroidJUnit4::class)
class ButtonToastTest {
  val database = Database()
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun saveButtonIsDisplayed() {
    // Arrange
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          3, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("last_button").performClick()
    //  toasts are not composable in nature, which makes them difficult to test within the Jetpack
    // Compose framework.
  }
}

@RunWith(AndroidJUnit4::class)
class EditEventScreenTestTest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPanels() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }
    // Move to the Second Panel
    composeTestRule.onNodeWithTag("next_button").assertExists().performClick()
    // Return to the First Panel
    composeTestRule.onNodeWithTag("previous_button").assertExists().performClick()
    // Check that we have moved to then previous screen
    composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }

  @Test
  fun testLocationSelector() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("LocationComponent").assertIsDisplayed()
  }

  @Test
  fun testSecondPanelContent() {
    var tagsLegendS = ""
    var publicLegendS = ""

    // Given
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          1, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))

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
  fun testInputIntoTextFields() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithTag("add_a_title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
    composeTestRule.onNodeWithTag("previous_button").assertDoesNotExist()
  }

  @Test
  fun testUIHelpingFunctions() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          2, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithContentDescription("logistics_title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("parking_title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("beds_title").assertIsDisplayed()
  }

  @Test
  fun testSociLalMediaPanelUI() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EditEventScreen(
          3, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("social_media_title").assertIsDisplayed()

    for (sm in SupportedSocialMedia) {
      if (sm.testTag != "spotify_input") {
        composeTestRule.onNodeWithTag(sm.testTag).performScrollTo().assertIsDisplayed()
        val testInput = "test ${sm.testTag}"
        composeTestRule.onNodeWithTag(sm.testTag).performTextInput(testInput)
        composeTestRule.onNodeWithTag(sm.testTag).performImeAction()
        composeTestRule.onNodeWithTag(sm.testTag).assertExists().assertTextContains(testInput)
      }
    }

    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()

    composeTestRule.onNodeWithTag("last_button").assertIsDisplayed()
  }
}
