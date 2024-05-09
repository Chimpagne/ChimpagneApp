package com.monkeyteam.chimpagne

import androidx.compose.runtime.getValue
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
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModelFactory
import org.junit.Assert.*
import org.junit.Before
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

  @Before
  fun init() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[0])
  }

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

    // You can add more detailed tests here for interactions and assertions
  }

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

    composeTestRule.onNodeWithTag("add_groceries_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_groceries_button").performClick()
    // Assert that the SupplyPopup is displayed after clicking the button
    composeTestRule.onNodeWithTag("groceries_title").assertIsDisplayed()

    composeTestRule.onNodeWithTag("add_groceries_button").performClick()
    // Fill in the necessary fields in the SupplyPopup
    composeTestRule.onNodeWithTag("supplies_description_field").performTextInput("New Item")
    composeTestRule.onNodeWithTag("supplies_quantity_field").performTextInput("5")
    composeTestRule.onNodeWithTag("supplies_unit_field").performTextInput("pcs")
    composeTestRule.onNodeWithTag("supplies_add_button").performClick()
    // Assert that the new item is added to the list
    composeTestRule.onNodeWithText("New Item").assertIsDisplayed()
    composeTestRule.onNodeWithText("5 pcs").assertIsDisplayed()
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
      EventCreationScreen(3, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    val value = "4"
    composeTestRule.onNodeWithTag("n_parking").assertExists()
    composeTestRule.onNodeWithTag("n_parking").performTextInput(value)

    val valueBed = "2"
    composeTestRule.onNodeWithTag("n_beds").assertExists()
    composeTestRule.onNodeWithTag("n_beds").performTextInput(valueBed)

    composeTestRule.onNodeWithTag("create_event_button").assertDoesNotExist()
    composeTestRule.onNodeWithTag("next_button").performClick()
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
      EventCreationScreen(3, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithTag("logistics_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("parking_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("beds_title").assertIsDisplayed()
  }

  @Test
  fun testSocialMediaPanelUIEdit() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(4, navActions, viewModel(factory = EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("social_media_title").assertIsDisplayed()

    val testAccountString = "test Account"
    composeTestRule.onNodeWithTag("instagram_input").assertExists()
    composeTestRule.onNodeWithTag("instagram_input").performTextInput(testAccountString)

    val testCodeString = "whatsapp invite"
    composeTestRule.onNodeWithTag("whatsapp_input").assertExists()
    composeTestRule.onNodeWithTag("whatsapp_input").performTextInput(testCodeString)

    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()
  }
}
