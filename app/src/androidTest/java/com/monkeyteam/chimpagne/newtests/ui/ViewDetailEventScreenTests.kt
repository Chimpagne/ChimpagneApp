package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
import com.monkeyteam.chimpagne.ui.IconInfo
import com.monkeyteam.chimpagne.ui.IconRow
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.components.eventview.EventActions
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
  fun testIconRow() {
    // Sample IconInfo data for testing
    val icons =
        listOf(
            IconInfo(
                icon = Icons.Default.Home,
                description = "Home",
                onClick = {},
                testTag = "home_icon"),
            IconInfo(
                icon = Icons.Default.Settings,
                description = "Settings",
                onClick = {},
                testTag = "settings_icon"))

    composeTestRule.setContent { IconRow(icons) }

    // Check if both icons are displayed
    composeTestRule.onNodeWithTag("home_icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("settings_icon").assertIsDisplayed()

    // Check if text descriptions are displayed
    composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

    // Check if click actions work
    composeTestRule.onNodeWithTag("home_icon").assertHasClickAction()
    composeTestRule.onNodeWithTag("settings_icon").assertHasClickAction()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun qrCodeGeneration_displaysQRCode() {
    val testEventId = "1234"

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
  fun generalTextTestTitle() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun generalTextTestTags() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("tag list").assertIsDisplayed()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun generalTextTestGuests() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("number_of_guests").assertExists()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun generalTextTestDate() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }
    composeTestRule.onNodeWithTag("event date").assertIsDisplayed()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun generalTextTestDescription() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("description").assertExists()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun TestEventActions() {

    val event = TEST_EVENTS[2]

    val eventVM = EventViewModel(event.id, database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})
    accountManager.signInTo(TEST_ACCOUNTS[1])

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventActions(
          navObject = navActions,
          eventViewModel = eventVM,
          isUserLoggedIn = true,
          showToast = {},
          showPromptLogin = {})
    }

    // Checking the presence of icons and their click actions
    composeTestRule
        .onNodeWithTag("edit")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("manage staff")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("supplies")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("polls")
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testUserIsInEvent() {
    val event = TEST_EVENTS[2]

    val eventVM = EventViewModel(event.id, database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})
    accountManager.signInTo(TEST_ACCOUNTS[1])

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule
        .onNodeWithTag("share")
        .performScrollTo()
        .performScrollTo()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("share").performClick()

    Thread.sleep(2000)
    accountViewModel.logoutFromChimpagneAccount()
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

    composeTestRule.onNodeWithTag("go_back_button").performScrollTo().performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testShareButton() {
    val event = TEST_EVENTS[2]

    val eventVM = EventViewModel(event.id, database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})
    accountManager.signInTo(TEST_ACCOUNTS[1])

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("share").performScrollTo().assertHasClickAction()
    composeTestRule.onNodeWithTag("share").performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testEditButton() {

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
        composable(Route.EDIT_EVENT_SCREEN + "/${eventVM.uiState.value.id}") {
          EventScreen(navObject = navActions, eventViewModel = eventVM, accountViewModel)
        }
      }
    }

    composeTestRule.onNodeWithTag("edit").performScrollTo().assertHasClickAction().performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testSuppliesButton() {
    val event = TEST_EVENTS[0]

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})
    accountManager.signInTo(TEST_ACCOUNTS[1])

    while (accountViewModel.uiState.value.loading) {}

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      NavHost(
          navController = navController,
          startDestination = Route.MY_EVENTS_SCREEN + "/${eventVM.uiState.value.id}") {
            composable(Route.MY_EVENTS_SCREEN + "/${eventVM.uiState.value.id}") {
              EventScreen(navActions, eventVM, accountViewModel)
            }
            composable(Route.SUPPLIES_SCREEN + "/${eventVM.uiState.value.id}") {
              EventScreen(navObject = navActions, eventVM, accountViewModel)
            }
          }
    }

    composeTestRule
        .onNodeWithTag("supplies")
        .performScrollTo()
        .assertHasClickAction()
        .performClick()
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testPollsButton() {
    val event = TEST_EVENTS[0]

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})
    accountManager.signInTo(TEST_ACCOUNTS[1])

    while (accountViewModel.uiState.value.loading) {}

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("polls").performScrollTo().assertHasClickAction().performClick()
  }

  @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

      NavHost(navController = navController!!, startDestination = Route.MY_EVENTS_SCREEN) {
        composable(Route.MY_EVENTS_SCREEN) { EventScreen(navActions, eventVM, accountViewModel) }
        composable(Route.MANAGE_STAFF_SCREEN + "/${eventVM.uiState.value.id}") {
          ManageStaffScreen(
              navObject = navActions, eventViewModel = eventVM, accountViewModel = accountViewModel)
        }
      }
    }

    composeTestRule
        .onNodeWithTag("manage staff")
        .performScrollTo()
        .assertHasClickAction()
        .performClick()
  }
}
