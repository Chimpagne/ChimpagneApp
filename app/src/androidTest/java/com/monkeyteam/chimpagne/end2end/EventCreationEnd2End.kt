package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventCreationEnd2End() {
  val database = Database()
  val accountViewModel = AccountViewModel(database = database)

  val test_account = TEST_ACCOUNTS[2]

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun init() {
    initializeTestDatabase()
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
    every { FirebaseAuth.getInstance().currentUser?.uid } returns test_account.firebaseAuthUID
  }

  @Test
  fun end2endTest() {
    lateinit var navController: NavHostController

    composeTestRule.setContent {
      navController = rememberNavController()
      NavigationGraph(
          navController = navController, accountViewModel = accountViewModel, database = database)
    }

    Thread.sleep(3000)

    assertEquals(test_account, accountViewModel.uiState.value.currentUserAccount)
    composeTestRule.onNodeWithTag("organize_event_button").performClick()
    composeTestRule.onNodeWithTag("organize_event_button").performClick()

    eventCreationTestPart(composeTestRule)
    Thread.sleep(3000)
    assertEquals(Route.HOME_SCREEN, navController.currentDestination?.route)

    composeTestRule.onNodeWithTag("open_events_button").performClick()
    composeTestRule.onNodeWithTag("a created event").performClick()
    assertTrue(
        navController.currentDestination!!.route!!.startsWith(Route.VIEW_DETAIL_EVENT_SCREEN))
  }
}

fun eventCreationTestPart(composeTestRule: ComposeContentTestRule) {
  composeTestRule.onNodeWithTag("add_a_title").performTextInput("Banana Party")
  composeTestRule.onNodeWithTag("next_button").performClick()
  composeTestRule.onNodeWithTag("next_button").performClick()
  composeTestRule.onNodeWithTag("next_button").performClick()
  composeTestRule.onNodeWithTag("next_button").performClick()
  composeTestRule.onNodeWithTag("last_button").performClick()
}
