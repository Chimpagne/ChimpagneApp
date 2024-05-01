package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewDetailEventScreenTests {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
  }

  @Test
  fun generalTextTest() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("event title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tag list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("number of guests").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("event date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
  }

  @Test
  fun testNavigationBackFunctionality() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("go back").assertHasClickAction()
    composeTestRule.onNodeWithTag("go back").performClick()
  }

  @Test
  fun testLeaveButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("leave").assertHasClickAction()
    composeTestRule.onNodeWithTag("leave").performClick()
  }

  @Test
  fun testEditButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, true)
    }

    composeTestRule.onNodeWithTag("edit").assertHasClickAction()
    composeTestRule.onNodeWithTag("edit").performClick()
  }

  @Test
  fun testChatButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("chat").assertHasClickAction()
    composeTestRule.onNodeWithTag("chat").performClick()
  }

  @Test
  fun testLocationButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("location").assertHasClickAction()
    composeTestRule.onNodeWithTag("location").performClick()
  }

  @Test
  fun testSuppliesButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("supplies").assertHasClickAction()
    composeTestRule.onNodeWithTag("supplies").performClick()
  }

  @Test
  fun testPollsButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("polls").assertHasClickAction()
    composeTestRule.onNodeWithTag("polls").performClick()
  }

  @Test
  fun testCarPoolingButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("car pooling").assertHasClickAction()
    composeTestRule.onNodeWithTag("car pooling").performClick()
  }
}
