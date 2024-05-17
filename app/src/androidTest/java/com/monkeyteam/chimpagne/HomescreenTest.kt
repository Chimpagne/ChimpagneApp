package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
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
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
  val database = Database()

  @Test
  fun TestButtonsAreDisplayed() {
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
