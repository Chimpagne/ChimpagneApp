package com.monkeyteam.chimpagne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.EventDetailSheet
import com.monkeyteam.chimpagne.ui.FindEventFormScreen
import com.monkeyteam.chimpagne.ui.FindEventMapScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModelFactory
import org.junit.Rule
import org.junit.Test

class FindEventScreenTest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayTitle() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("find_event_title").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displaySearchButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("button_search").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayLocationInput() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("input_location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displaySelectLocationButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("sel_location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displaySelectDateSelector() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("sel_date").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayMapScreen() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, FindEventsViewModel(database = database))
    }

    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()
  }

  @Test
  fun testEventDetailSheetDisplay() {
    val sampleEvent = ChimpagneEvent(title = "banana", description = "MONKEY")

    composeTestRule.setContent {
      EventDetailSheet(sampleEvent, viewModel(factory = FindEventsViewModelFactory(database)))
    }

    // Assert that event details are displayed correctly
    composeTestRule.onNodeWithText(sampleEvent.title).assertIsDisplayed()
    composeTestRule.onNodeWithText(sampleEvent.description).assertIsDisplayed()
    // Add more assertions as needed for other event details
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun findEventFormScreen_DisplayedCorrectly() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventFormScreen(navActions, FindEventsViewModel(database = database), {}, {})
    }

    // Check if the location selector is displayed
    composeTestRule.onNodeWithTag("sel_location").assertExists()

    // Check if the date selector is displayed
    composeTestRule.onNodeWithTag("sel_date").assertExists()

    composeTestRule
        .onNodeWithTag("input_location", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertIsEnabled()

    // Simulate clicking the search button
    composeTestRule.onNodeWithTag("button_search").performClick()
  }
}
