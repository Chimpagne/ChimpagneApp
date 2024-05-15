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
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewDetailEventScreenTests {

  val database = Database()
  val accountViewModel = AccountViewModel(database = database)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
  }

  @Test
  fun qrCodeGeneration_displaysQRCode() {
    val testEventId = "12345"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, EventViewModel(testEventId, database), accountViewModel)
    }

    composeTestRule.onNodeWithContentDescription("Scan QR").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Scan QR").performClick()

    composeTestRule.onNodeWithTag("close_button").performClick()
  }

  @Test
  fun generalTextTest() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("event title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tag list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("number of guests").assertIsDisplayed()
    composeTestRule.onNodeWithTag("event date").assertIsDisplayed()
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
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("go back").assertHasClickAction()
    composeTestRule.onNodeWithTag("go back").performClick()
  }

  @Test
  fun testLeaveButton() {
    database.accountManager.signInTo(TEST_ACCOUNTS[0])
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("leave").assertHasClickAction()
  }

  @Test
  fun testShareButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("share").assertHasClickAction()
    composeTestRule.onNodeWithTag("share").performClick()
  }

  @Test
  fun testEditButton() {

    database.accountManager.signInTo(TEST_ACCOUNTS[1])

    val event = TEST_EVENTS[0]
    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("edit").assertHasClickAction().performClick()
  }

  @Test
  fun testBedButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("bed_reservation").assertHasClickAction().performClick()
  }

  @Test
  fun testParkingButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("parking").assertHasClickAction().performClick()
  }

  @Test
  fun testSuppliesButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("supplies").assertHasClickAction().performClick()
  }

  @Test
  fun testPollsButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("polls").assertHasClickAction().performClick()
  }

  @Test
  fun testCarPoolingButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("car pooling").assertHasClickAction().performClick()
  }

  @Test
  fun testManageStaffButton() {
    database.accountManager.signInTo(TEST_ACCOUNTS[1])
    val event = TEST_EVENTS[0]
    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("manage staff").assertHasClickAction().performClick()
  }
}
