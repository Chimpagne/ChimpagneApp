package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.event.polls.PollsAndVotingScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PollsAndVotingScreenTests {
  val database = Database()
  private val ownerEvents = TEST_EVENTS[0]
  private val notOwnerEvent = TEST_EVENTS[3]

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[1])
  }

  @Test
  fun generalUITest() {
    val eventVM = EventViewModel(ownerEvents.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      PollsAndVotingScreen(eventVM) { navActions.goBack() }
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("create poll button").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("back").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("poll legend text").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty poll list").assertIsDisplayed()
  }

  @Test
  fun createPollFailureTest() {
    val eventVM = EventViewModel(ownerEvents.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      PollsAndVotingScreen(eventVM) { navActions.goBack() }
    }

    composeTestRule
        .onNodeWithContentDescription("create poll button")
        .assertIsDisplayed()
        .performClick()

    composeTestRule.onNodeWithTag("confirm poll button").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("cancel poll button").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("empty poll list").assertIsDisplayed()
  }

  @Test
  fun createPollSuccessTest() {
    val eventVM = EventViewModel(ownerEvents.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      PollsAndVotingScreen(eventVM) { navActions.goBack() }
    }

    composeTestRule
        .onNodeWithContentDescription("create poll button")
        .assertIsDisplayed()
        .performClick()

    composeTestRule.onNodeWithTag("poll title field").assertExists().performTextInput("title")
    composeTestRule.onNodeWithTag("poll query field").assertExists().performTextInput("query")

    composeTestRule.onNodeWithTag("poll option 1 field").assertExists().performTextInput("option 1")
    composeTestRule.onNodeWithTag("poll option 2 field").assertExists().performTextInput("option 2")

    composeTestRule.onNodeWithContentDescription("add option button").assertExists().performClick()

    composeTestRule.onNodeWithTag("poll option 3 field").assertExists().performTextInput("option 3")

    composeTestRule
        .onNodeWithContentDescription("remove option button")
        .assertExists()
        .performClick()

    composeTestRule.onNodeWithTag("confirm poll button").assertExists().performClick()

    Thread.sleep(2 * SLEEP_AMOUNT_MILLIS)

    composeTestRule.onNodeWithTag("a poll").assertIsDisplayed()
    initializeTestDatabase()
  }
}
