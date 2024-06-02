package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.AppLayout
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventActions {
  val event =
      ChimpagneEvent(
          id = "EVENT_ACTIONS",
          title = "for testing",
          description = "I love bananas",
          Location("EPFL", 46.519130, 6.567580),
          public = true,
          tags = listOf("bananas", "monkeys"),
          guests = mapOf("PRINCE" to true),
          staffs = emptyMap(),
          startsAtTimestamp = buildTimestamp(10, 5, 2024, 15, 15),
          endsAtTimestamp = buildTimestamp(11, 8, 2024, 15, 15),
          ownerId = "JUAN",
          supplies = emptyMap(),
          parkingSpaces = 10,
          beds = 5)

  val accounts =
      listOf(
          ChimpagneAccount(
              firebaseAuthUID = "PRINCE",
              firstName = "Monkey",
              lastName = "Prince",
              joinedEvents = mapOf("EVENT_ACTIONS" to true)),
          ChimpagneAccount(
              firebaseAuthUID = "JUAN",
              firstName = "Juan",
              lastName = "Litalien",
              joinedEvents = mapOf("EVENT_ACTIONS" to true)))

  val TIMEOUT_MILLIS: Long = 5000 // in milliseconds

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun init() {
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns accounts[1].firebaseAuthUID

    initializeTestDatabase(events = listOf(event), accounts = accounts)
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun eventActionsEndToEndTest() {

    lateinit var navController: NavHostController
    lateinit var accountViewModel: AccountViewModel

    composeTestRule.setContent {
      navController = rememberNavController()
      accountViewModel = viewModel(factory = AccountViewModelFactory(database))

      AppLayout(
          navController = navController, accountViewModel = accountViewModel, database = database)
    }
    composeTestRule.waitUntil(TIMEOUT_MILLIS) {
      navController.currentDestination?.route == Route.HOME_SCREEN
    }
    composeTestRule.onNodeWithTag("open_events_button").assertExists().performClick()

    composeTestRule.waitUntil(TIMEOUT_MILLIS) {
      navController.currentDestination?.route == Route.MY_EVENTS_SCREEN
    }
    Thread.sleep(2 * SLEEP_AMOUNT_MILLIS)
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("a created event"), TIMEOUT_MILLIS)
    composeTestRule.onNodeWithText(event.title, useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("a created event").performClick()

    composeTestRule.waitUntil(2 * TIMEOUT_MILLIS) {
      navController.currentDestination?.route!! == Route.EVENT_SCREEN + "/{EventID}"
    }
    Thread.sleep(SLEEP_AMOUNT_MILLIS)
    /*
    composeTestRule.waitUntilAtLeastOneExists(hasTestTag("polls"), TIMEOUT_MILLIS)
    composeTestRule.onNodeWithTag("polls").performScrollTo().performClick()

    composeTestRule.waitUntil(TIMEOUT_MILLIS) {
      navController.currentDestination?.route == Route.POLLS_SCREEN
    }*/
  }
}
