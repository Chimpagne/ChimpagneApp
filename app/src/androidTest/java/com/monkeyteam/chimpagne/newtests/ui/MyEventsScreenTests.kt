package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModelFactory
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
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun generalTextTest() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, viewModel(factory = MyEventsViewModelFactory(database)))
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Created Events").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Joined Events").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testNavigationBackFunctionality() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, viewModel(factory = MyEventsViewModelFactory(database)))
    }

    composeTestRule.onNodeWithContentDescription("back").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testUserWithJoinedAndCreatedEvents() {

    val accountViewModel = AccountViewModel(database = database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[0].firebaseAuthUID, {}, {})

    while (accountViewModel.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, viewModel(factory = MyEventsViewModelFactory(database)))
    }

    composeTestRule.onNodeWithTag("a created event").assertIsDisplayed()
    composeTestRule.onNodeWithTag("a joined event").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testUserWithNoEvents() {

    val accountViewModel = AccountViewModel(database = database)

    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[1].firebaseAuthUID, {}, {})

    while (accountViewModel.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      MyEventsScreen(navActions, viewModel(factory = MyEventsViewModelFactory(database)))
    }

    composeTestRule.onNodeWithTag("empty join event list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty create event list").assertIsDisplayed()
  }
}
