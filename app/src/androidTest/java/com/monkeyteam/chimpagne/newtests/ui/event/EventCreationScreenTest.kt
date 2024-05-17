package com.monkeyteam.chimpagne

import android.icu.util.Calendar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import com.monkeyteam.chimpagne.ui.components.SupportedSocialMedia
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import junit.framework.TestCase
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
      EventCreationScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    // Move to the Second Panel

    composeTestRule.onNodeWithTag("next_button").performClick()
    // Return to the First Panel
    composeTestRule.onNodeWithTag("previous_button").performClick()
  }

  @Test
  fun testLocationSelector() {
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("LocationComponent").assertIsDisplayed()
  }

  @Test
  fun testPanel1() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          1, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
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
      EventCreationScreen(
          1, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))

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

      EventCreationScreen(
          1, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
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
      EventCreationScreen(
          2, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }
    // composeTestRule.onNodeWithTag("tag_field").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("supplies_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_supplies_button").assertIsDisplayed()

    composeTestRule.onNodeWithTag("add_supplies_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_supplies_button").performClick()
    // Assert that the SupplyPopup is displayed after clicking the button
    composeTestRule.onNodeWithContentDescription("supplies_title").assertIsDisplayed()

    composeTestRule.onNodeWithTag("add_supplies_button").performClick()
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
      EventCreationScreen(
          0, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
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
      EventCreationScreen(
          3, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    val value = "4"
    composeTestRule.onNodeWithTag("n_parking").assertExists()
    composeTestRule.onNodeWithTag("n_parking").performTextInput(value)

    val valueBed = "2"
    composeTestRule.onNodeWithTag("n_beds").assertExists()
    composeTestRule.onNodeWithTag("n_beds").performTextInput(valueBed)
    composeTestRule.onNodeWithTag("last_button").assertDoesNotExist()
    composeTestRule.onNodeWithTag("next_button").performClick()
  }

  @Test
  fun testInvalidPanel() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          19, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithText("Title").assertDoesNotExist()
    composeTestRule.onNodeWithText("Description").assertDoesNotExist()
  }

  @Test
  fun testUIHelpingFunctions() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          3, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }
    composeTestRule.onNodeWithContentDescription("logistics_title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("parking_title").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("beds_title").assertIsDisplayed()
  }

  @Test
  fun testSocialMediaPanelUIEdit() {
    // Now we do this to go the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      EventCreationScreen(
          4, navActions, viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
    }

    composeTestRule.onNodeWithTag("social_media_title").assertIsDisplayed()

    for (sm in SupportedSocialMedia) {
      composeTestRule.onNodeWithTag(sm.testTag).assertExists().assertIsDisplayed()
      val testInput = "test ${sm.testTag}"
      composeTestRule.onNodeWithTag(sm.testTag).performTextInput(testInput)
      composeTestRule.onNodeWithTag(sm.testTag).assertExists().assertTextContains(testInput)
    }

    composeTestRule.onNodeWithTag("next_button").assertDoesNotExist()
  }

  @Test
  fun defaultStartAndEndTest() {
    val eventUIState = EventViewModel.EventUIState()
    // Get the current time
    val currentTime = Calendar.getInstance()

    // Create expected start and end times
    val expectedStartTime =
        Calendar.getInstance().apply {
          time = currentTime.time
          add(Calendar.HOUR_OF_DAY, 1)
        }
    val expectedEndTime =
        Calendar.getInstance().apply {
          time = currentTime.time
          add(Calendar.HOUR_OF_DAY, 2)
        }

    // Define a tolerance value in milliseconds (e.g., 10 second)
    val toleranceMillis = 10000L

    // Assert that the start time is approximately 1 hour after the current time
    assertApproximatelyEqual(expectedStartTime, eventUIState.startsAtCalendarDate, toleranceMillis)

    // Assert that the end time is approximately 2 hours after the current time
    assertApproximatelyEqual(expectedEndTime, eventUIState.endsAtCalendarDate, toleranceMillis)
  }

  private fun assertApproximatelyEqual(
      expected: Calendar,
      actual: java.util.Calendar,
      toleranceMillis: Long
  ) {
    val diffMillis = kotlin.math.abs(expected.timeInMillis - actual.timeInMillis)
    TestCase.assertTrue(
        "Expected: $expected, Actual: $actual, Diff: $diffMillis ms", diffMillis <= toleranceMillis)
  }
}
