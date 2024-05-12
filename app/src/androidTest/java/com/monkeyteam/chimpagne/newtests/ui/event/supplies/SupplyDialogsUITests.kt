package com.monkeyteam.chimpagne.newtests.ui.event.supplies

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.event.details.supplies.GuestSupplyDialog
import com.monkeyteam.chimpagne.ui.event.details.supplies.StaffSupplyDialog
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupplyDialogsUITests {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun guestSupplyDialogAssignTest() {
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
  fun guestSupplyDialogUnassignTest() {
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

  @Test
  fun staffSupplyDialogTest() {
    val supply = ChimpagneSupply()

    var dismissRequested = false
    var deleteButtonClicked = false
    var editButtonClicked = false
    composeTestRule.setContent {
      StaffSupplyDialog(
          supply,
          { editButtonClicked = true },
          { deleteButtonClicked = true },
          "hector",
          hashMapOf("hector" to ChimpagneAccount("hector")),
          onDismissRequest = { dismissRequested = true })
    }

    composeTestRule.onNodeWithTag("cancel_supply_button").performClick()
    assertTrue(dismissRequested)

    composeTestRule.onNodeWithTag("delete_supply_button").performClick()
    composeTestRule.onNodeWithTag("cancel_delete_button").isDisplayed()
    composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
    assertTrue(deleteButtonClicked)

    composeTestRule.onNodeWithTag("edit_supply_button").performClick()
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()
    composeTestRule.onNodeWithTag("save_supply_button").performClick()
    assertTrue(editButtonClicked)
  }
}
