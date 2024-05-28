package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventCreation {
  val timeout: Long = 3000 // in milliseconds

  val database = Database()
  val account =
      ChimpagneAccount(
          firebaseAuthUID = "ovoland",
          firstName = "Graphics",
          lastName = "Expert",
          joinedEvents = hashMapOf())

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun init() {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns account.firebaseAuthUID

    initializeTestDatabase(accounts = listOf(account))
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun eventCreationEnd2End() {
    val eventName = "OUR FIRST END TO END TEST"

    lateinit var navController: NavHostController
    lateinit var accountViewModel: AccountViewModel

    composeTestRule.setContent {
      navController = rememberNavController()
      accountViewModel = viewModel(factory = AccountViewModelFactory(database))

      NavigationGraph(
          navController = navController, accountViewModel = accountViewModel, database = database)
    }
    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.HOME_SCREEN
    }

    composeTestRule.onNodeWithTag("organize_event_button").performClick()
    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.EVENT_CREATION_SCREEN
    }
    composeTestRule.onNodeWithTag("add_a_title").performTextInput(eventName)
    composeTestRule.onNodeWithTag("next_button").performClick()
    composeTestRule.onNodeWithTag("next_button").performClick()
    composeTestRule.onNodeWithTag("next_button").performClick()
    composeTestRule.onNodeWithTag("next_button").performClick()
    composeTestRule.onNodeWithTag("last_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.HOME_SCREEN
    }
    composeTestRule.onNodeWithTag("open_events_button").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route == Route.MY_EVENTS_SCREEN
    }
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("a created event"), timeout)
    composeTestRule.onNodeWithTag("a created event").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route?.startsWith(Route.EVENT_SCREEN) ?: false
    }
    composeTestRule.onNodeWithText(eventName, useUnmergedTree = true).assertIsDisplayed()
  }
}
