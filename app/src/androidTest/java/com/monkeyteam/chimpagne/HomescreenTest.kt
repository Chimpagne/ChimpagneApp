package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomescreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  val database = Database()

  fun allowPermissionsIfNeeded() {
    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    try {
      val allowPermissions: UiObject = uiDevice.findObject(UiSelector().text("ALLOW"))
      if (allowPermissions.exists()) {
        allowPermissions.click()
      }
    } catch (e: UiObjectNotFoundException) {
      // Handle the exception
    }
  }

  @Test
  fun TestButtonsAreDisplayed() {
    allowPermissionsIfNeeded()
    val accountViewModel = AccountViewModel(database = database)
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      HomeScreen(navActions, accountViewModel)
    }

    composeTestRule.onNodeWithTag("open_events_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("discover_events_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("organize_event_button").assertIsDisplayed()
  }
}
