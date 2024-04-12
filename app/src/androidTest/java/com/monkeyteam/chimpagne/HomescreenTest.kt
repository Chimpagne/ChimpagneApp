package com.monkeyteam.chimpagne

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomescreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun TestButtonsAreDisplayed() {
        // Start on the correct screen
        composeTestRule.setContent {
            val navController = rememberNavController()
            val navActions = NavigationActions(navController)
            HomeScreen(navActions)
        }

        composeTestRule.onNodeWithTag("open_events_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("discover_events_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("organize_event_button").assertIsDisplayed()

        //composeTestRule.onNodeWithTag("discover_events_button").performClick()
        //composeTestRule.onNodeWithTag("find_event_title").assertIsDisplayed()
    }
}