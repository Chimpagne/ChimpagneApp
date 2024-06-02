package com.monkeyteam.chimpagne.end2end

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.AppLayout
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class EventDiscovery {
    val TIMEOUT_MILLIS: Long = 5000

        val database = Database()
        val account =
            ChimpagneAccount(
                firebaseAuthUID = "calkestis",
                firstName = "Cal",
                lastName = "Kestis"
            )

    val tomorrow = buildCalendar(Timestamp.now())

    init {
        tomorrow.add(Calendar.DATE, 1)
    }
        val events = listOf(
            ChimpagneEvent(
                id = "EVENT_TO_DISCOVER_1",
                title = "Jedi fallen order",
                location = Location("EPFL", 46.519124, 6.567593),
                public = true,
                tags = listOf("starwars", "jediapprentice"),
                startsAtTimestamp = Timestamp.now(),
                endsAtTimestamp = buildTimestamp(tomorrow),
                ownerId = "JUAN"
            ),
//            ChimpagneEvent(
//                id = "EVENT_TO_DISCOVER_2",
//                title = "Jedi survivor",
//                location = Location("EPFL", 46.519124, 6.567593),
//                public = true,
//                tags = listOf("starwars", "jedimaster"),
//                startsAtTimestamp = Timestamp.now(),
//                endsAtTimestamp = Timestamp.now(),
//                ownerId = "JUAN"
//            )
        )

        @get:Rule
        val composeTestRule = createComposeRule()

        @get:Rule
        val mRuntimePermissionRule: GrantPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

        @Composable
        fun LocationSelectorTestView(
            selectedLocation: Location?,
            updateSelectedLocation: (Location) -> Unit
        ) {
            LocationSelector(
                selectedLocation = selectedLocation, updateSelectedLocation = updateSelectedLocation)
        }

        @Before
        fun init() {
            mockkStatic(FirebaseAuth::class)
            every { FirebaseAuth.getInstance().currentUser } returns mockk(relaxed = true)
            every { FirebaseAuth.getInstance().currentUser?.uid } returns account.firebaseAuthUID

            initializeTestDatabase(events = events, accounts = listOf(account))
        }

        @OptIn(ExperimentalTestApi::class)
        @Test
        fun eventDiscoveryEnd2End() {
            lateinit var navController: NavHostController
            lateinit var accountViewModel: AccountViewModel

            composeTestRule.setContent {
                navController = rememberNavController()
                accountViewModel = viewModel(factory = AccountViewModelFactory(database))

                AppLayout(
                    navController = navController,
                    accountViewModel = accountViewModel,
                    database = database
                )
            }

            composeTestRule.waitUntil(TIMEOUT_MILLIS) {
                navController.currentDestination?.route == Route.HOME_SCREEN
            }
            composeTestRule.onNodeWithTag("discover_events_button").performClick()
            composeTestRule.waitUntil(TIMEOUT_MILLIS) {
                navController.currentDestination?.route == Route.FIND_AN_EVENT_SCREEN
            }

            composeTestRule.onNodeWithText("Search for a location").assertExists().performTextInput("EPFL")
            composeTestRule.onNodeWithTag("SearchIcon").performClick()
            composeTestRule.waitUntil(TIMEOUT_MILLIS) {
                composeTestRule.onAllNodesWithTag("location_possibility").fetchSemanticsNodes().isNotEmpty()
            }
            composeTestRule.onAllNodesWithTag("location_possibility").onFirst().performClick()

            composeTestRule.onNodeWithTag("find_slider").assertExists().performTouchInput {
                this.swipeRight()
            }
            composeTestRule.onNodeWithTag("button_search").performClick()

//            composeTestRule.waitUntilAtLeastOneExists(hasTestTag("cluster"), TIMEOUT_MILLIS)
            var ok = false
            runBlocking {
                launch {
                    delay(TIMEOUT_MILLIS)
                    ok = true
                }
            }

//            composeTestRule.onNodeWithTag("discover_events_button").assertIsDisplayed()
            composeTestRule.waitUntil(TIMEOUT_MILLIS) { ok }
//            Thread.sleep(TIMEOUT_MILLIS)

            composeTestRule.onNodeWithTag("ggle_maps").performClick()
            Thread.sleep(TIMEOUT_MILLIS)
            composeTestRule.onNodeWithTag("EVENT_TO_DISCOVER_1").performClick()
            composeTestRule.waitUntilAtLeastOneExists(hasTestTag("join_button"), TIMEOUT_MILLIS) /*doesnt work, cannot figure out why*/
        }
}