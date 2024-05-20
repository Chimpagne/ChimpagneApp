package com.monkeyteam.chimpagne

import android.location.LocationManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
import org.mockito.Mockito

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

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun requestLocationPermissionFalseTest() {
    val locationManager = Mockito.mock(LocationManager::class.java)
    Mockito.`when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        .thenReturn(false)

    val accountViewModel = AccountViewModel(database = database)
    // Start on the correct screen
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      HomeScreen(navActions, accountViewModel)
    }

    // Perform the action that checks GPS status
    composeTestRule.onNodeWithTag("request_location_permission_button").performClick()

    // Assertions or further actions can be added here
  }
}
