package com.monkeyteam.chimpagne

import android.location.LocationManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.EventScreen
import com.monkeyteam.chimpagne.ui.FindEventFormScreen
import com.monkeyteam.chimpagne.ui.FindEventMapScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.QRCodeScanner
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class FindEventScreenTest {

  val database = Database()
  private val accountViewModel = AccountViewModel(database = database)

  var accountManager = database.accountManager

  private val anAccount = TEST_ACCOUNTS[1]

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.CAMERA)

  lateinit var fusedLocationProviderClient: FusedLocationProviderClient

  lateinit var locationManager: LocationManager

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun requestLocationPermissionTest() {
    val locationManager = mock(LocationManager::class.java)
    `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      FindEventFormScreen(navActions, FindEventsViewModel(database = Database()), {}, {}, {})
    }

    // Perform the action that checks GPS status
    composeTestRule.onNodeWithTag("request_location_permission_button").performClick()

    // Assertions or further actions can be added here
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun requestLocationPermissionFalseTest() {
    val locationManager = mock(LocationManager::class.java)
    `when`(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false)

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      FindEventFormScreen(navActions, FindEventsViewModel(database = Database()), {}, {}, {})
    }

    // Perform the action that checks GPS status
    composeTestRule.onNodeWithTag("request_location_permission_button").performClick()

    // Assertions or further actions can be added here
  }

  @Test
  fun testQRCodeScanner() {

    composeTestRule.setContent { QRCodeScanner({}, {}) }

    composeTestRule.onNodeWithTag("qr_code_scanner").assertIsDisplayed()
    composeTestRule.onNodeWithTag("camera_preview").assertIsDisplayed()
    composeTestRule.onNodeWithTag("close_button").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testGoBackFunctionality() {
    val database = Database()
    val findViewModel = FindEventsViewModel(database)
    val accountViewModel = AccountViewModel(database)

    var navController: NavHostController? = null

    composeTestRule.setContent {
      navController = rememberNavController()
      val navActions = NavigationActions(navController!!)

      FindEventMapScreen({ navController!!.popBackStack() }, findViewModel, {})
    }

    // Simulate the goBack action by clicking the back icon
    composeTestRule.onNodeWithTag("go_back").performClick()
    assertTrue(navController!!.previousBackStackEntry == null)
  }

  @OptIn(ExperimentalFoundationApi::class)
  @Test
  fun testJoinEventFunctionalityJoinAsGuest() {
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)

    // Ensure the event ID is correctly formatted
    val eventViewModel = EventViewModel("FIRST_EVENT", database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventScreen(
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel,
          navObject = navActions)
    }
    while (accountViewModel.uiState.value.loading) {}
    while (eventViewModel.uiState.value.id.isEmpty()) {}
    // Check if the button exists before performing a click
    val joinButtonNode = composeTestRule.onNodeWithTag("join_button")
    joinButtonNode.assertExists("Join button does not exist")

    // Perform the click action on the button
    joinButtonNode.performClick()

    composeTestRule.waitUntil {
      eventViewModel.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.GUEST
    }

    // Check the role after clicking the join button
    assertTrue(eventViewModel.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.GUEST)
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Test
  fun testJoinEventFunctionalityAlreadyStaff() {
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)

    // Ensure the event ID is correctly formatted
    val eventViewModel = EventViewModel("FOURTH_EVENT", database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventScreen(
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel,
          navObject = navActions)
    }
    while (accountViewModel.uiState.value.loading) {}
    while (eventViewModel.uiState.value.id.isEmpty()) {}

    assertTrue(eventViewModel.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.STAFF)
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  @Test
  fun testJoinEventFunctionalityAlreadyOwner() {
    val myAccount = TEST_ACCOUNTS[1]
    accountManager.signInTo(myAccount)

    // Ensure the event ID is correctly formatted
    val eventViewModel = EventViewModel("SECOND_EVENT", database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      EventScreen(
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel,
          navObject = navActions)
    }
    while (accountViewModel.uiState.value.loading) {}
    while (eventViewModel.uiState.value.id.isEmpty()) {}

    assertTrue(eventViewModel.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.OWNER)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayTitle() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(
          navActions,
          eventViewModel = EventViewModel("1", database),
          FindEventsViewModel(database = database),
          accountViewModel)
    }

    composeTestRule.onNodeWithTag("screen title").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayLocationIcon() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(
          navActions,
          eventViewModel = EventViewModel("1", database),
          FindEventsViewModel(database = database),
          accountViewModel)
    }

    composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayLocationInput() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(
          navActions,
          eventViewModel = EventViewModel("1", database),
          FindEventsViewModel(database = database),
          accountViewModel)
    }

    composeTestRule.onNodeWithTag("input_location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displaySearchButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(
          navActions,
          eventViewModel = EventViewModel("1", database),
          FindEventsViewModel(database = database),
          accountViewModel)
    }

    composeTestRule.onNodeWithTag("button_search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_search").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayMapScreen() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, FindEventsViewModel(database = database), {})
    }

    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun findEventFormScreen_DisplayedCorrectly() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventFormScreen(navActions, FindEventsViewModel(database = database), {}, {}, {})
    }

    // Check if the location selector is displayed
    composeTestRule.onNodeWithTag("input_location").assertExists()

    // Check if the date selector is displayed
    composeTestRule.onNodeWithTag("sel_date").assertExists()

    composeTestRule
        .onNodeWithTag("input_location", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertIsEnabled()

    // Simulate clicking the search button
    composeTestRule.onNodeWithTag("button_search").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun findEventFormScreen_DisplayQR() {

    val fvm = FindEventsViewModel(database = database)

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventFormScreen(navActions, fvm, {}, {}, {})
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithContentDescription("Scan QR").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Scan QR").performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("qr_code_scanner").assertIsDisplayed()
    composeTestRule.onNodeWithTag("close_button").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testNavigationBackFunctionality() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      FindEventFormScreen(navActions, FindEventsViewModel(database = database), {}, {}, {})
    }

    composeTestRule.onNodeWithContentDescription("back").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testMainFindEventScreen() {
    val findViewModel = FindEventsViewModel(database)

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)
      MainFindEventScreen(
          navObject = navigationActions,
          eventViewModel = EventViewModel("1", database),
          findViewModel = findViewModel,
          accountViewModel = accountViewModel)
    }

    // Assert that initially, the FindEventFormScreen is displayed
    composeTestRule.onNodeWithTag("find_event_form_screen").assertExists()

    composeTestRule.onNodeWithTag("button_search").performClick()
  }
}
