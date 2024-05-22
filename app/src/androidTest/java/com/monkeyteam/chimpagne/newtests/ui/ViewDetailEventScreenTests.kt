package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.event.details.supplies.SuppliesScreen
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
  fun generalTextTestTitle() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("event title").assertIsDisplayed()
  }

  @Test
  fun generalTextTestTags() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("tag list").assertIsDisplayed()
  }

  @Test
  fun generalTextTestGuests() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("number_of_guests").assertIsDisplayed()
  }

  @Test
  fun generalTextTestDate() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }

    composeTestRule.onNodeWithTag("event date").assertIsDisplayed()
  }

  @Test
  fun generalTextTestDescription() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ViewDetailEventScreen(navActions, eventVM, accountViewModel)
    }
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

    var navController: NavHostController? = null

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      navController = rememberNavController()
      val navActions = NavigationActions(navController!!)
      NavHost(navController = navController!!, startDestination = Route.VIEW_DETAIL_EVENT_SCREEN) {
        composable(Route.VIEW_DETAIL_EVENT_SCREEN) {
          ViewDetailEventScreen(navActions, eventVM, accountViewModel)
        }
        composable(Route.EDIT_EVENT_SCREEN + "/${eventVM.uiState.value.id}") {
          EditEventScreen(navObject = navActions, eventViewModel = eventVM)
        }
      }
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

    composeTestRule.onNodeWithTag("accommodation").assertHasClickAction().performClick()
  }

  @Test
  fun testSuppliesButton() {
    val event = TEST_EVENTS[0]

    val eventVM = EventViewModel(event.id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      NavHost(
          navController = navController,
          startDestination = Route.VIEW_DETAIL_EVENT_SCREEN + "/${eventVM.uiState.value.id}") {
            composable(Route.VIEW_DETAIL_EVENT_SCREEN + "/${eventVM.uiState.value.id}") {
              ViewDetailEventScreen(navActions, eventVM, accountViewModel)
            }
            composable(Route.SUPPLIES_SCREEN + "/${eventVM.uiState.value.id}") {
              SuppliesScreen(navObject = navActions, eventVM, accountViewModel)
            }
          }
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

  @OptIn(ExperimentalMaterial3Api::class)
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

      NavHost(navController = navController!!, startDestination = Route.VIEW_DETAIL_EVENT_SCREEN) {
        composable(Route.VIEW_DETAIL_EVENT_SCREEN) {
          ViewDetailEventScreen(navActions, eventVM, accountViewModel)
        }
        composable(Route.MANAGE_STAFF_SCREEN + "/${eventVM.uiState.value.id}") {
          ManageStaffScreen(
              navObject = navActions, eventViewModel = eventVM, accountViewModel = accountViewModel)
        }
      }
    }

    composeTestRule.onNodeWithTag("manage staff").assertHasClickAction().performClick()
  }
}
