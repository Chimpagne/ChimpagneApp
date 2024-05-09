package com.monkeyteam.chimpagne.newtests.ui.supplies

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.screens.supplies.GuestSupplyDialog
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuestSupplyDialogUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun supplyGuestAssignTest() {
    val assignedSupply =
        ChimpagneSupply(
            id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf("hector" to true))
    val unassignedSupply =
        ChimpagneSupply(id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf())
    var supply = unassignedSupply
    composeTestRule.setContent {
      GuestSupplyDialog(
          supply,
          { if (it) supply = assignedSupply else supply = unassignedSupply },
          "hector",
          hashMapOf("hector" to ChimpagneAccount("hector")),
          onDismissRequest = {})
    }

    composeTestRule.onNodeWithTag("guest_supply_dialog").isDisplayed()
    composeTestRule.onNodeWithTag("nobody_assigned").isDisplayed()
    composeTestRule.onNodeWithTag("guest_supply_assign").performClick()
    assertEquals(assignedSupply, supply)
  }

  @Test
  fun supplyGuestUnassignTest() {
    val assignedSupply =
        ChimpagneSupply(
            id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf("hector" to true))
    val unassignedSupply =
        ChimpagneSupply(id = "a", unit = "bananas", quantity = 3, assignedTo = hashMapOf())
    var supply = assignedSupply
    composeTestRule.setContent {
      GuestSupplyDialog(
          supply,
          { if (it) supply = assignedSupply else supply = unassignedSupply },
          "hector",
          hashMapOf("hector" to ChimpagneAccount("hector")),
          onDismissRequest = {})
    }

    composeTestRule.onNodeWithTag("guest_supply_dialog").isDisplayed()
    composeTestRule.onNodeWithTag("nobody_assigned").isDisplayed()
    composeTestRule.onNodeWithTag("guest_supply_unassign").performClick()
    assertEquals(unassignedSupply, supply)
  }
}
