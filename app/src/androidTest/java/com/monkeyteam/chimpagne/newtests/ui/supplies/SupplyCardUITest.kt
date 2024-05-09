package com.monkeyteam.chimpagne.newtests.ui.supplies

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.screens.supplies.SupplyCard
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupplyCardUITest {

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
}
