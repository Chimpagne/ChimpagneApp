package com.monkeyteam.chimpagne.newtests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndToEndCreateAndEditEvent {

  @get:Rule val composeTestRule = createComposeRule()
  val database = Database()

  @Test
  fun TestButtonsAreDisplayed() {
    val accountViewModel = AccountViewModel(database = database)
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      HomeScreen(navActions, accountViewModel)
    }
    composeTestRule.onNodeWithTag("organize_event_button").assertIsDisplayed()
    // Click on the button with test tag "organize_event_button"
    composeTestRule.onNodeWithTag("organize_event_button").performClick()

    // Generate a random, 20 characters string and write it in variable _eventTestTitle
    val _eventTestTitle =
        (1..20).map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }.joinToString("")
    Thread.sleep(1000)

    composeTestRule.onNodeWithTag("add_a_title").assertIsDisplayed()
    // Write in the input add_a_title
    // composeTestRule.onNodeWithTag("add_a_title").performTextInput(_eventTestTitle)
    /*
    // Click on the button with test tag "next_button" 3 times, waiting 1 second between each click
    repeat(3) {
        composeTestRule.onNodeWithTag("next_button").performClick()
        Thread.sleep(1000) // Wait for 1 second
    }

    // Click on the button with test tag "create_event_button"
    composeTestRule.onNodeWithTag("create_event_button").performClick()
    Thread.sleep(10000)
    // Click on the button with test tag "organize_event_button"
    composeTestRule.onNodeWithTag("open_events_button").performClick()
    Thread.sleep(60000)
    */
  }
}
