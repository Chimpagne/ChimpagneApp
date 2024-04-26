package com.monkeyteam.chimpagne

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import org.junit.Rule
import org.junit.Test

class LocationSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLocationSelectorInput() {
        val selectedLocation = Location()
        val updateSelectedLocation: (Location) -> Unit = {}

        composeTestRule.setContent {
            LocationSelectorTestView(
                selectedLocation = selectedLocation,
                updateSelectedLocation = updateSelectedLocation
            )
        }
        composeTestRule.onNodeWithText("Search for a location").performTextInput("New York")

        composeTestRule.onNode(hasTestTag("LocationComponent")).assertIsDisplayed()
    }

    @Test
    fun testLocationSelection() {
        var selectedLocation by mutableStateOf(Location("MockLocation"))
        val updateSelectedLocation: (Location) -> Unit = { location ->
            selectedLocation = location
        }

        composeTestRule.setContent {
            LocationSelectorTestView(
                selectedLocation = selectedLocation,
                updateSelectedLocation = updateSelectedLocation
            )
        }

        composeTestRule.onNodeWithText(selectedLocation.name).performClick()
    }

    @Test
    fun testSearchIconVisibilityAndFunctionality() {
        composeTestRule.setContent {
            LocationSelectorTestView(
                selectedLocation = null,
                updateSelectedLocation = {}
            )
        }
        composeTestRule.onNodeWithTag("SearchIcon").assertDoesNotExist()
        composeTestRule.onNodeWithText("Search for a location").performTextInput("New York")
        composeTestRule.onNodeWithTag("SearchIcon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchIcon").performClick()
    }

}

@Composable
fun LocationSelectorTestView(
    selectedLocation: Location?,
    updateSelectedLocation: (Location) -> Unit
) {
    LocationSelector(
        selectedLocation = selectedLocation,
        updateSelectedLocation = updateSelectedLocation
    )
}

