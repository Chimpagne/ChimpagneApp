package com.monkeyteam.chimpagne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.ui.EventDetailSheet
import com.monkeyteam.chimpagne.ui.FindEventFormScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import org.junit.Rule
import org.junit.Test

class FindEventScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testEventDetailSheetDisplay() {
    val sampleEvent = ChimpagneEvent(title="banana", description = "MONKEY" )

    composeTestRule.setContent { EventDetailSheet(sampleEvent) }

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
      FindEventFormScreen(navActions, FindEventsViewModel(), {}, {})
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
