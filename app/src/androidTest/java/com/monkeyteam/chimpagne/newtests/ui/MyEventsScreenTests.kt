package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyEventsScreenTests {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[0])
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun generalTextTest() {

    val myEventVM = MyEventsViewModel(database)

    while (myEventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, myEventVM)
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Created Events").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Joined Events").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testNavigationBackFunctionality() {

    val myEventVM = MyEventsViewModel(database)

    while (myEventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, myEventVM)
    }

    composeTestRule.onNodeWithTag("go_back_button").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testUserWithJoinedAndCreatedEvents() {

    val accountViewModel = AccountViewModel(database = database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[0].firebaseAuthUID, {}, {})

    while (accountViewModel.uiState.value.loading) {}

    val myEventVM = MyEventsViewModel(database)

    while (myEventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, myEventVM)
    }

    composeTestRule.onAllNodesWithTag("a created event").onFirst().assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("a joined event").onFirst().assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testUserWithNoEvents() {

    val accountViewModel = AccountViewModel(database = database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})

    while (accountViewModel.uiState.value.loading) {}

    val myEventVM = MyEventsViewModel(database)

    while (myEventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, myEventVM)
    }

    composeTestRule.onNodeWithTag("empty join event list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty create event list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty_past_events_list").assertIsDisplayed()
  }
}
