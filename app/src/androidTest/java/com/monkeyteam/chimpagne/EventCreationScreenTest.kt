package com.monkeyteam.chimpagne

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.components.SupplyPopup
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModelFactory
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InstrumentEventCreationScreenTest {
  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.monkeyteam.chimpagne", appContext.packageName)
  }
}

@RunWith(AndroidJUnit4::class)
class EventCreationScreenTest {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPanels() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    // Move to the Second Panel

    composeTestRule.onNodeWithTag("next_button").performClick()
    // composeTestRule.onNodeWithTag("next_button")
    // composeTestRule.onNodeWithText("More event infos").assertIsDisplayed()

    // Return to the First Panel
    composeTestRule.onNodeWithTag("previous_button").performClick()

    // composeTestRule.onNodeWithText("Title").assertIsDisplayed()
  }

  @Test
  fun testLocationSelector() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("LocationComponent").assertIsDisplayed()
  }

  @Test
  fun testPanel1() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
  }

  @Test
  fun testSecondPanelContent() {
    var tagsLegendS = ""
    var publicLegendS = ""

    // Given
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))

      val context = LocalContext.current
      tagsLegendS = context.getString(R.string.event_creation_screen_tags_legend)
      publicLegendS = context.getString(R.string.event_creation_screen_public_legend)
    }

    // When - Then
    composeTestRule.onNodeWithText(tagsLegendS).assertIsDisplayed()
    composeTestRule.onNodeWithText(publicLegendS).assertIsDisplayed()
  }

  @Test
  fun testDeleteAllSupplyItemsFromEventCreationScreen() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(2, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    // When
    composeTestRule.onNodeWithTag("add_groceries_button").performClick()
    composeTestRule.onNodeWithTag("supplies_description_field").performTextInput("Test Supply 1")
    composeTestRule.onNodeWithTag("supplies_quantity_field").performTextInput("5")
    composeTestRule.onNodeWithTag("supplies_unit_field").performTextInput("units")
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()

    composeTestRule.onNodeWithTag("add_groceries_button").performClick()
    composeTestRule.onNodeWithTag("supplies_description_field").performTextInput("Test Supply 2")
    composeTestRule.onNodeWithTag("supplies_quantity_field").performTextInput("10")
    composeTestRule.onNodeWithTag("supplies_unit_field").performTextInput("kg")
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()

    // Then
    composeTestRule.onNodeWithText("Test Supply 1").assertIsDisplayed()
    composeTestRule.onNodeWithText("5 units").assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Supply 2").assertIsDisplayed()
    composeTestRule.onNodeWithText("10 kg").assertIsDisplayed()

    // When
    composeTestRule.onNodeWithTag("Test Supply 1").performClick()
    composeTestRule.onNodeWithTag("Test Supply 2").performClick()
    Thread.sleep(2000)
    // Then
    composeTestRule.onNodeWithText("Test Supply 1").assertDoesNotExist()
    composeTestRule.onNodeWithText("5 units").assertDoesNotExist()
    composeTestRule.onNodeWithText("Test Supply 2").assertDoesNotExist()
    composeTestRule.onNodeWithText("10 kg").assertDoesNotExist()
  }

  @Test
  fun testSupplyPopup() {
    var descriptionLabelS = ""
    var quantityLabelS = ""
    var unitLabelS = ""
    var cancelButtonS = ""
    var addButtonS = ""

    // Given
    composeTestRule.setContent {
      val context = LocalContext.current
      descriptionLabelS = context.getString(R.string.supplies_description)
      quantityLabelS = context.getString(R.string.supplies_quantity)
      unitLabelS = context.getString(R.string.supplies_unit)
      cancelButtonS = context.getString(R.string.chimpagne_cancel)
      addButtonS = context.getString(R.string.chimpagne_add)

      var showPopup by remember { mutableStateOf(true) }
      if (showPopup) {
        SupplyPopup(onDismissRequest = { showPopup = false }, onSave = { _ -> })
      }
    }

    // When - Then
    composeTestRule.onNodeWithText(descriptionLabelS).assertIsDisplayed()
    composeTestRule.onNodeWithText(quantityLabelS).assertIsDisplayed()
    composeTestRule.onNodeWithText(unitLabelS).assertIsDisplayed()
    composeTestRule.onNodeWithText(cancelButtonS).assertIsDisplayed()
    composeTestRule.onNodeWithText(addButtonS).assertIsDisplayed()

    // Enter values in the text fields
    composeTestRule.onNodeWithTag("supplies_description_field").performTextInput("Test Description")
    composeTestRule.onNodeWithTag("supplies_quantity_field").performTextInput("10")
    composeTestRule.onNodeWithTag("supplies_unit_field").performTextInput("kg")

    // Click the "Add" button
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()

    // Verify that the popup is dismissed
    composeTestRule.onNodeWithText(descriptionLabelS).assertDoesNotExist()
  }

  /*

  //TODO: use the test tags !
  @Test
  fun testMakeEventPublicButtonShowsToast() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventCreationScreen(1, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    // Tags (comma-separated)
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
    composeTestRule.onNodeWithText("Logistics").assertDoesNotExist()
    composeTestRule.onNodeWithText("Parking").assertDoesNotExist()
    composeTestRule.onNodeWithText("Beds").assertDoesNotExist()
    // This will attempt to click the button and create a Toast.
    // Note that testing the actual visibility of a Toast is beyond the scope of Compose UI Tests.
    composeTestRule.onNodeWithText("Make this event public").performClick()
  }
  */

  @Test
  fun testPanel2() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(2, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    // composeTestRule.onNodeWithTag("tag_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("groceries_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_groceries_button").assertIsDisplayed()
  }

  @Test
  fun testInputIntoTextFields() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(0, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    val title = "Sample Event Title"
    composeTestRule.onNodeWithTag("add_a_title").performTextInput(title)

    composeTestRule.onNodeWithText(title).assertIsDisplayed()
    composeTestRule.onNodeWithTag("previous_button").assertDoesNotExist()
  }

  @Test
  fun testFourthPanel() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    val value = "4"
    composeTestRule.onNodeWithTag("n_parking").assertExists()
    composeTestRule.onNodeWithTag("n_parking").performTextInput(value)

    composeTestRule.onNodeWithText(value).assertIsDisplayed()

    val valueBed = "2"
    composeTestRule.onNodeWithTag("n_beds").assertExists()
    composeTestRule.onNodeWithTag("n_beds").performTextInput(valueBed)

    composeTestRule.onNodeWithText(valueBed).assertIsDisplayed()
    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()

    composeTestRule.onNodeWithTag("create_event_button").performClick()
  }

  @Test
  fun testInvalidPanel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          19, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
  }

  @Test
  fun testUIHelpingFunctions() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithTag("logistics_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("parking_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("beds_title").assertIsDisplayed()
  }
}
