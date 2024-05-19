package com.monkeyteam.chimpagne.newtests.ui.event.supplies

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.event.details.supplies.SuppliesScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuppliesScreenUITests {

  val database = Database()

  val ownerAccount = TEST_ACCOUNTS[1]
  val guestAccount = TEST_ACCOUNTS[0]
  val event = TEST_EVENTS[2]

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @Test
  fun staffViewTest() {
    val eventViewModel = EventViewModel(event.id, database)
    val accountViewModel = AccountViewModel(database)

    var loading = true
    accountViewModel.loginToChimpagneAccount(
        ownerAccount.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    while (loading) {}

    eventViewModel.fetchEvent(
        onSuccess = { accountViewModel.fetchAccounts(listOf(event.owner.firebaseAuthUID)) })

    while (eventViewModel.uiState.value.loading && accountViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    var you = ""
    composeTestRule.setContent {
      you = stringResource(id = R.string.chimpagne_you)
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      SuppliesScreen(
          navObject = navActions,
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel)
    }

    composeTestRule.onNodeWithTag("edit_supply_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("guest_supply_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("staff_supply_dialog").assertDoesNotExist()

    composeTestRule.onNodeWithTag("assigned_nobody").onChildAt(1).assertDoesNotExist()
    composeTestRule.onNodeWithTag("supply_add").performClick()
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    composeTestRule.onNodeWithTag("assigned_nobody").onChildAt(1).performClick()
    composeTestRule.onNodeWithTag("staff_supply_dialog").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "${ownerAccount.firstName} ${ownerAccount.lastName} ($you)", useUnmergedTree = true)
        .performClick()
    composeTestRule.onNodeWithTag("save_supply_button").performClick()
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    composeTestRule.onNodeWithTag("assigned_you").onChildAt(0).assertIsDisplayed()
  }

  @Test
  fun guestViewTest() {
    val eventViewModel = EventViewModel(event.id, database)
    val accountViewModel = AccountViewModel(database)

    var loading = true
    accountViewModel.loginToChimpagneAccount(
        guestAccount.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    composeTestRule.waitUntil(timeoutMillis = 5000) { !loading }

    eventViewModel.fetchEvent(
        onSuccess = { accountViewModel.fetchAccounts(listOf(event.owner.firebaseAuthUID)) })

    composeTestRule.waitUntil(timeoutMillis = 5000) {
      !eventViewModel.uiState.value.loading && !accountViewModel.uiState.value.loading
    }

    var you = ""
    composeTestRule.setContent {
      you = stringResource(id = R.string.chimpagne_you)
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      SuppliesScreen(
          navObject = navActions,
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel)
    }

    composeTestRule.onNodeWithTag("edit_supply_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("guest_supply_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("staff_supply_dialog").assertDoesNotExist()

    composeTestRule.onNodeWithTag("assigned_you").assertDoesNotExist()
    composeTestRule.onNodeWithTag("assigned_nobody").onChildAt(0).performClick()
    composeTestRule.onNodeWithTag("guest_supply_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("guest_supply_assign").performClick()
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    composeTestRule.onNodeWithTag("guest_supply_dialog").assertDoesNotExist()
    composeTestRule.onNodeWithTag("assigned_you").onChildAt(0).performClick()
  }
}
