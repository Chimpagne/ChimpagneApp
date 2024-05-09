package com.monkeyteam.chimpagne.newtests.ui.supplies

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.screens.supplies.SuppliesScreen
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SuppliesScreenUITest {

  val database = Database()

  @Before
  fun init() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[0])
  }

  @get:Rule val composeTestRule = createComposeRule()

  private val supply =
      ChimpagneSupply(
          id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf("hector" to true))

  @Test
  fun supplyScreenNothingDisplayedTest() {
    val eventViewModel = EventViewModel(TEST_EVENTS[3].id, database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(TEST_ACCOUNTS[0].firebaseAuthUID, {}, {})
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      SuppliesScreen(navObject = navActions, eventViewModel, accountViewModel = accountViewModel)
    }
    while (eventViewModel.uiState.value.loading && accountViewModel.uiState.value.loading){}

    composeTestRule.onNodeWithTag("supply_nothing", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("supply_add").performClick()
    composeTestRule.onNodeWithTag("edit_supply_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()

    while (eventViewModel.uiState.value.loading && accountViewModel.uiState.value.loading){}
    composeTestRule.onNodeWithTag("supply_nothing", useUnmergedTree = true).assertDoesNotExist()
    composeTestRule.onNodeWithTag("supply_not_assigned", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("supply_card", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("staff_supply_dialog").isDisplayed()
  }
}
