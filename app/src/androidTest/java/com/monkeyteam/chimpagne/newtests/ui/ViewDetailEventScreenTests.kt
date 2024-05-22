package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.EventScreen
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
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
  var accountManager = database.accountManager

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun qrCodeGeneration_displaysQRCode() {
    val testEventId = "12345"
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, EventViewModel(testEventId, database), accountViewModel)
    }

    composeTestRule.onNodeWithContentDescription("Scan QR").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Scan QR").performClick()

    composeTestRule.onNodeWithTag("close_button").performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun generalTextTest() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("event_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("tag_list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("number of guests").assertIsDisplayed()
    composeTestRule.onNodeWithTag("event date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description").assertIsDisplayed()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testNavigationBackFunctionality() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("go_back").assertHasClickAction()
    composeTestRule.onNodeWithTag("go_back").performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testLeaveButton() {
    database.accountManager.signInTo(TEST_ACCOUNTS[0])
    val event = TEST_EVENTS[2]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("leave").assertHasClickAction()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testShareButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("share").assertHasClickAction()
    composeTestRule.onNodeWithTag("share").performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testEditButton() {

    // Logging as owner of the event
    val myAccount = TEST_ACCOUNTS[1]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[1]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    // Is displayed for the owner of the event
    composeTestRule.onNodeWithTag("edit").performScrollTo().assertHasClickAction()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testBedButton() {
    // Logging in a user to an event he joined to display the buttons
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[4]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    composeTestRule
        .onNodeWithTag("bed_reservation")
        .performScrollTo()
        .assertHasClickAction()
        .performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testParkingButton() {
    // Logging in a user to an event he joined to display the buttons
    val myAccount = TEST_ACCOUNTS[1]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[1]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    composeTestRule.onNodeWithTag("parking").performScrollTo().assertHasClickAction().performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testSuppliesButton() {
    // Logging in a user to an event he joined to display the buttons
    val myAccount = TEST_ACCOUNTS[1]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[1]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    composeTestRule.onNodeWithTag("supplies").performScrollTo().assertHasClickAction()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testPollsButton() {

    // Logging in a user to an event he joined to display the buttons
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[4]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    composeTestRule.onNodeWithTag("polls").performScrollTo().assertHasClickAction().performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testCarPoolingButton() {
    // Logging in a user to an event he joined to display the buttons
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    val event = TEST_EVENTS[4]
    val eventVM = EventViewModel(event.id, database)
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    while (accountViewModel.uiState.value.loading) {}
    while (eventVM.uiState.value.id.isEmpty()) {}

    composeTestRule
        .onNodeWithTag("car pooling")
        .performScrollTo()
        .assertHasClickAction()
        .performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Test
  fun testManageStaffButton() {
    database.accountManager.signInTo(TEST_ACCOUNTS[1])
    val event = TEST_EVENTS[0]
    val eventVM = EventViewModel(event.id, database)
    var navController: NavHostController? = null
    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      navController = rememberNavController()
      val navActions = NavigationActions(navController!!)

      NavHost(navController = navController!!, startDestination = Route.EVENT_SCREEN) {
        composable(Route.EVENT_SCREEN) { EventScreen(navActions, eventVM, accountViewModel) }
        composable(Route.MANAGE_STAFF_SCREEN + "/${eventVM.uiState.value.id}") {
          ManageStaffScreen(
              navObject = navActions, eventViewModel = eventVM, accountViewModel = accountViewModel)
        }
      }
    }

    composeTestRule.onNodeWithTag("manage staff").assertHasClickAction().performClick()
  }
}
