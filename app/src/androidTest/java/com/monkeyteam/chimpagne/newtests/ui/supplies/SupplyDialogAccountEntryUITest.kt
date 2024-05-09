package com.monkeyteam.chimpagne.newtests.ui.supplies

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.ui.screens.supplies.SupplyDialogAccountEntry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupplyDialogAccountEntryUITest {

  @get:Rule val composeTestRule = createComposeRule()

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
