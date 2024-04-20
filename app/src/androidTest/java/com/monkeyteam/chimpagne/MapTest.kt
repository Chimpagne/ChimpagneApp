package com.monkeyteam.chimpagne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {

    @get:Rule val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun checkUILoading(){
        composeTestRule.setContent {
            MapContainer(
                cameraPositionState = rememberCameraPositionState(),
                isMapInitialized = false,
                bottomSheetState = rememberBottomSheetScaffoldState().bottomSheetState,
                onMarkerClick = {},
                events = emptyMap()
            )
        }

        composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun checkUIShowing(){

        /*val chimpagneEvent1 = ChimpagneEvent(
            id = "1",
            title = "Event 1",
            description = "Description 1",
            location = Location("what1",0.0, 0.0),
        )
        val chimpagneEvent2 = ChimpagneEvent(
            id = "2",
            title = "Event 2",
            description = "Description 2",
            location = Location("what2",0.0, 0.0),
        )
        val twoChimpagneEvents = mapOf(
            "1" to chimpagneEvent1,
            "2" to chimpagneEvent2
        )
        var counter = 0*/
        composeTestRule.setContent {
            MapContainer(
                cameraPositionState = rememberCameraPositionState(),
                isMapInitialized = true,
                bottomSheetState = rememberBottomSheetScaffoldState().bottomSheetState,
                onMarkerClick = {/*counter++*/},
                events = emptyMap()
            )
        }

        composeTestRule.onNodeWithTag("ggle_maps").assertExists().assertIsDisplayed()
        /*for (event in twoChimpagneEvents.values){
            composeTestRule.onNodeWithContentDescription(event.title).assertExists().assertIsDisplayed()
            val currentCount = counter
            composeTestRule.onNodeWithTag(event.title).performClick()
            assert(counter == currentCount + 1)
        }*/
    }

}