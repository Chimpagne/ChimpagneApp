package com.monkeyteam.chimpagne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.DetailScreenSheet
import com.monkeyteam.chimpagne.ui.FindEventFormScreen
import com.monkeyteam.chimpagne.ui.FindEventMapScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.QRCodeAnalyser
import com.monkeyteam.chimpagne.ui.utilities.QRCodeScanner
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FindEventScreenTest {

  val database = Database()
  private val accountViewModel = AccountViewModel(database = database)

  var accountManager = database.accountManager

  val anAccount = TEST_ACCOUNTS[1]

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.ACCESS_FINE_LOCATION,
          android.Manifest.permission.ACCESS_COARSE_LOCATION,
          android.Manifest.permission.CAMERA)

  @Before
  fun init() {
    initializeTestDatabase()
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

      FindEventMapScreen(
          { navController!!.popBackStack() }, findViewModel, accountViewModel, navActions)
    }

    // Simulate the goBack action by clicking the back icon
    composeTestRule.onNodeWithTag("go_back").performClick()
    assertTrue(navController!!.previousBackStackEntry == null)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testJoinEventFunctionalityNotLoggedInUser() {
    val database = Database()
    val findViewModel = FindEventsViewModel(database)
    val accountViewModel = AccountViewModel(database)
    val sampleEvent =
        ChimpagneEvent(id = "sample123", title = "Sample Event", description = "Sample Description")
    findViewModel.setResultEvents(mapOf(sampleEvent.id to sampleEvent))

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, findViewModel, accountViewModel, navActions)
    }

    composeTestRule.onNodeWithTag("join_button").performClick()

    // Should be false because the user is not logged in (guest) so it will not trigger the
    // joinEvent function hence no uiState will load anything
    assertFalse(findViewModel.uiState.value.loading)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testJoinEventFunctionalityAlreadyGuest() {
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(myAccount)

    val findViewModel = FindEventsViewModel(database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    val joinedEvent = TEST_EVENTS[2]
    findViewModel.setResultEvents(mapOf(joinedEvent.id to joinedEvent))

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, findViewModel, accountViewModel, navActions)
    }

    composeTestRule.onNodeWithTag("join_button").performClick()

    assertTrue(joinedEvent.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.GUEST)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testJoinEventFunctionalityAlreadyStaff() {
    val myAccount = TEST_ACCOUNTS[0]
    accountManager.signInTo(anAccount)

    val findViewModel = FindEventsViewModel(database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    val staffEvent = TEST_EVENTS[4]
    findViewModel.setResultEvents(mapOf(staffEvent.id to staffEvent))

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, findViewModel, accountViewModel, navActions)
    }

    composeTestRule.onNodeWithTag("join_button").performClick()

    assertTrue(staffEvent.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.STAFF)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun testJoinEventFunctionalityAlreadyOwner() {
    val myAccount = TEST_ACCOUNTS[1]
    accountManager.signInTo(myAccount)

    val findViewModel = FindEventsViewModel(database)
    val accountViewModel = AccountViewModel(database)
    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    val ownerEvent = TEST_EVENTS[2]
    findViewModel.setResultEvents(mapOf(ownerEvent.id to ownerEvent))

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, findViewModel, accountViewModel, navActions)
    }

    composeTestRule.onNodeWithTag("join_button").performClick()

    assertTrue(ownerEvent.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.OWNER)
  }

  //  @OptIn(ExperimentalMaterial3Api::class)
  //  @Test
  //  fun testJoinEvent_NotInEvent() {
  //    val myAccount = TEST_ACCOUNTS[1]
  //    accountManager.signInTo(myAccount)
  //
  //    val findViewModel = FindEventsViewModel(database)
  //    val accountViewModel = AccountViewModel(database)
  //
  //    accountViewModel.loginToChimpagneAccount(myAccount.firebaseAuthUID, {}, {})
  //    while(accountViewModel.uiState.value.loading){}
  //
  //    val notJoinedEvent = TEST_EVENTS[3]
  //    findViewModel.setResultEvents(mapOf(notJoinedEvent.id to notJoinedEvent))
  //
  //    val eventVM = EventViewModel(notJoinedEvent.id, database)
  //    while(eventVM.uiState.value.loading){}
  //
  //    composeTestRule.setContent {
  //      val navController = rememberNavController()
  //      val navActions = NavigationActions(navController)
  //
  //      NavHost(navController = navController, startDestination = Route.FIND_AN_EVENT_SCREEN){
  //        composable(Route.FIND_AN_EVENT_SCREEN){
  //          FindEventMapScreen({}, findViewModel, accountViewModel, navActions)
  //        }
  //        composable(Route.VIEW_DETAIL_EVENT_SCREEN + "/${notJoinedEvent.id}"){
  //          ViewDetailEventScreen(navObject = navActions, eventViewModel = eventVM,
  // accountViewModel = accountViewModel)
  //        }
  //      }
  //    }
  //
  //    assertTrue(notJoinedEvent.getRole(myAccount.firebaseAuthUID) == ChimpagneRole.NOT_IN_EVENT)
  //
  //    composeTestRule.onNodeWithTag("join_button").performClick()
  //
  //  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayTitle() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database), accountViewModel)
    }

    composeTestRule.onNodeWithTag("find_event_title").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayLocationIcon() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database), accountViewModel)
    }

    composeTestRule.onNodeWithContentDescription("Location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayLocationInput() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database), accountViewModel)
    }

    composeTestRule.onNodeWithTag("input_location").assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displaySearchButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      MainFindEventScreen(navActions, FindEventsViewModel(database = database), accountViewModel)
    }
    composeTestRule.onNodeWithTag("sel_location").performClick()

    composeTestRule.onNodeWithTag("button_search").assertIsDisplayed()
    composeTestRule.onNodeWithTag("button_search").performClick()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun displayMapScreen() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)

      FindEventMapScreen({}, FindEventsViewModel(database = database), accountViewModel, navActions)
    }

    composeTestRule.onNodeWithTag("map_screen").assertIsDisplayed()
  }

  @Test
  fun testEventDetailSheetDisplay() {
    val sampleEvent = ChimpagneEvent(id = "houhouhou", title = "banana", description = "MONKEY")

    composeTestRule.setContent { DetailScreenSheet(event = sampleEvent) }

    // Assert that event details are displayed correctly
    composeTestRule.onNodeWithText(sampleEvent.title).assertIsDisplayed()
    composeTestRule.onNodeWithText(sampleEvent.description).assertIsDisplayed()
    // Add more assertions as needed for other event details
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
    composeTestRule.onNodeWithTag("sel_location").assertExists()

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
      val analyser = QRCodeAnalyser {}
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
          navObject = navigationActions, findViewModel = findViewModel, accountViewModel)
    }

    // Assert that initially, the FindEventFormScreen is displayed
    composeTestRule.onNodeWithTag("find_event_form_screen").assertExists()

    composeTestRule.onNodeWithTag("button_search").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("map_screen").assertExists()
  }
}
