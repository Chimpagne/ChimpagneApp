package com.monkeyteam.chimpagne.newtests.ui.event.supplies

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.ui.event.details.supplies.SupplyCard
import com.monkeyteam.chimpagne.ui.event.details.supplies.SupplyDialogAccountEntry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupplyScreenComponentsUITests {

  @get:Rule val composeTestRule = createComposeRule()

  private val supply =
      ChimpagneSupply(
          id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf("hector" to true))

  @Test
  fun supplyCardTest() {
    composeTestRule.setContent { SupplyCard(supply = supply) {} }
    composeTestRule.onNodeWithTag("supply_quantity_and_unit", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("supply_nb_assigned", useUnmergedTree = true).assertExists()
  }

  @Test
  fun supplyAccountEntryTest() {
    composeTestRule.setContent {
      SupplyDialogAccountEntry(
          account = TEST_ACCOUNTS[0],
          loggedUserUID = TEST_ACCOUNTS[0].firebaseAuthUID,
          showCheckBox = true)
    }

    composeTestRule
        .onNodeWithTag("supply_account_entry", useUnmergedTree = true)
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("supply_account_checkbox", useUnmergedTree = true).assertExists()
  }
}
