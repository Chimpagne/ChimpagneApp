package com.monkeyteam.chimpagne.newtests.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManageStaffScreenTests {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTests() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[1])
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun generalTextTest() {

    val eventVM = EventViewModel(TEST_EVENTS[0].id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ManageStaffScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Guest List").assertIsNotDisplayed()
    composeTestRule.onNodeWithContentDescription("floating button").assertHasClickAction()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testNavigationBackFunctionality() {

    val eventVM = EventViewModel(TEST_EVENTS[0].id, database)

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ManageStaffScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithContentDescription("back").assertHasClickAction()
    composeTestRule.onNodeWithContentDescription("back").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun fullFunctionalityTest() {

    val eventVM = EventViewModel(TEST_EVENTS[2].id, database)

    while (eventVM.uiState.value.loading) {}

    eventVM.fetchAccounts()

    while (eventVM.uiState.value.loading) {}

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      ManageStaffScreen(navActions, eventVM)
    }

    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty staff list").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("staff member").assertIsNotDisplayed()
    //composeTestRule.onNodeWithContentDescription("Guest List").assertIsNotDisplayed()
    //composeTestRule.onNodeWithTag("empty guest list").assertIsNotDisplayed()
    //composeTestRule.onNodeWithTag("guest member").assertIsNotDisplayed()

    composeTestRule.onNodeWithContentDescription("floating button").assertHasClickAction()
    composeTestRule.onNodeWithContentDescription("floating button").performClick()

    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty staff list").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("staff member").assertIsNotDisplayed()
    composeTestRule.onNodeWithContentDescription("Guest List").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("empty guest list").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("guest member").assertIsDisplayed()

    composeTestRule.onNodeWithTag("guest member").assertHasClickAction()
    composeTestRule.onNodeWithTag("guest member").performClick()

    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("empty staff list").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("staff member").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Guest List").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty guest list").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("guest member").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("staff member").assertHasClickAction()
    composeTestRule.onNodeWithTag("staff member").performClick()

    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty staff list").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("staff member").assertIsNotDisplayed()
    composeTestRule.onNodeWithContentDescription("Guest List").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("empty guest list").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("guest member").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("floating button").assertHasClickAction()
    composeTestRule.onNodeWithContentDescription("floating button").performClick()

    composeTestRule.onNodeWithContentDescription("Staff List").assertIsDisplayed()
    composeTestRule.onNodeWithTag("empty staff list").assertIsDisplayed()
    //composeTestRule.onNodeWithTag("staff member").assertIsNotDisplayed()
    //composeTestRule.onNodeWithContentDescription("Guest List").assertIsNotDisplayed()
    //composeTestRule.onNodeWithTag("empty guest list").assertIsNotDisplayed()
    //composeTestRule.onNodeWithTag("guest member").assertIsNotDisplayed()
  }
}
