package com.monkeyteam.chimpagne

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkUILoading() {
    composeTestRule.setContent {
      MapContainer(
          cameraPositionState = rememberCameraPositionState(),
          isMapInitialized = false,
          radius = 10.0,
          startingPosition = Location("EPFL", 46.518659400000004, 6.566561505148001),
          onMarkerClick = {},
          events = emptyMap())
    }

    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }
}
