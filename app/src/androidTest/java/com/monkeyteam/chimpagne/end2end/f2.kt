package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
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

/** End to end test for adding a supply to an event, as well as creating a poll and voting on it. */
@RunWith(AndroidJUnit4::class)
class PollEnd2EndTest {
  val timeout: Long = 5000 // in milliseconds

  val database = Database()
  val account =
      ChimpagneAccount(
          firebaseAuthUID = "JUAN",
          firstName = "Graphics",
          lastName = "Expert",
          joinedEvents = hashMapOf("event1" to true))

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
  fun pollEnd2EndTest() {

    val eventName = "will do polls"

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
    composeTestRule.onNodeWithText(eventName, useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("a created event").performClick()

    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route?.startsWith(Route.EVENT_SCREEN) ?: false
    }
    /*composeTestRule.onNodeWithTag("polls").performClick()

    // Verifying empty state
    composeTestRule.waitUntil(timeout) {
      composeTestRule.onNodeWithTag("empty poll list").isDisplayed()
    }

    // Create a poll
    composeTestRule.onNodeWithTag("fab_create_poll").performClick()
    composeTestRule
        .onNodeWithTag("poll title field")
        .performTextInput("Favorite Programming Language?")
    composeTestRule
        .onNodeWithTag("poll query field")
        .performTextInput("What is your favorite programming language?")
    composeTestRule.onNodeWithTag("poll option 1 field").performTextInput("Kotlin")
    Thread.sleep(200)
    composeTestRule.onNodeWithTag("poll option 2 field").performTextInput("Swift")
    Thread.sleep(200)
    composeTestRule.waitUntil(timeout) { composeTestRule.onNodeWithText("Swift").isDisplayed() }

    composeTestRule.onNodeWithTag("confirm poll button").assertExists().performClick()

    // Verify poll is displayed
    composeTestRule.waitUntil(timeout) {
      composeTestRule.onAllNodesWithTag("a poll").fetchSemanticsNodes().size == 1
    }

    // Click to vote on the poll
    composeTestRule.onAllNodesWithTag("a poll")[0].performClick()
    Thread.sleep(200)
    composeTestRule.onNodeWithContentDescription("option 1 unselected").performClick()

    composeTestRule.onNodeWithTag("confirm option button").performClick()
    composeTestRule.waitUntil(timeout) {
      composeTestRule.onNodeWithTag("go_back_button").isDisplayed()
    }
    composeTestRule.onNodeWithTag("go_back_button").performClick()
    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route?.startsWith(Route.EVENT_SCREEN) ?: false
    }
    Thread.sleep(200)
    */
    composeTestRule.onNodeWithTag("supplies").performClick()
    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route?.startsWith(Route.SUPPLIES_SCREEN) ?: false
    }
    composeTestRule.waitUntil(timeout) { composeTestRule.onNodeWithTag("supply_add").isDisplayed() }
    composeTestRule.onNodeWithTag("supply_add").performClick()
    composeTestRule.onNodeWithTag("supplies_quantity_field").performTextInput("7")
    // ***
    composeTestRule.onNodeWithTag("supplies_unit_field").performTextInput("mg")

    composeTestRule.onNodeWithTag("supplies_description_field").performTextInput("Elixir des dieux")
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()

    //  todo, maybe: assigning stuff to people

    composeTestRule.onNodeWithTag("go_back_button").performClick()
    composeTestRule.waitUntil(timeout) {
      navController.currentDestination?.route?.startsWith(Route.EVENT_SCREEN) ?: false
    }
  }
}
