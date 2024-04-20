package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContainer(
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    isMapInitialized: Boolean = false,
    bottomSheetState: SheetState,
    onMarkerClick: (Marker) -> Unit,
    events: Map<String, ChimpagneEvent>
) {

  val dynamicBottomPadding =
      when (bottomSheetState.targetValue) {
        SheetValue.Expanded -> 180.dp
        SheetValue.PartiallyExpanded -> 0.dp
        SheetValue.Hidden -> 0.dp
      }

  LaunchedEffect(events) {
    if (events.isNotEmpty()) {
      val latitudes = events.values.map { it.location.latitude }
      val longitudes = events.values.map { it.location.longitude }
      val minLat = latitudes.minOrNull()!!
      val maxLat = latitudes.maxOrNull()!!
      val minLon = longitudes.minOrNull()!!
      val maxLon = longitudes.maxOrNull()!!

      val latRange = maxLat - minLat
      val lonRange = maxLon - minLon

      val maxRange = max(latRange, lonRange)
      val centerLat = (maxLat + minLat) / 2
      val centerLon = (maxLon + minLon) / 2

      // Occupy 3/4 of the screen for accessibility
      // Shift of 1/8 of the screen
      val offset = maxRange / 8
      val adjustedCenterLat = centerLat + offset

      val newMinLat = adjustedCenterLat - maxRange / 2
      val newMaxLat = adjustedCenterLat + maxRange / 2
      val newMinLon = centerLon - maxRange / 2
      val newMaxLon = centerLon + maxRange / 2

      val bounds =
          LatLngBounds.Builder()
              .include(LatLng(newMinLat, newMinLon))
              .include(LatLng(newMaxLat, newMaxLon))
              .build()

      cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }
  }

  if (isMapInitialized) {

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier =
            Modifier.testTag("ggle_maps").fillMaxSize().padding(bottom = dynamicBottomPadding),
        uiSettings =
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false)) {
          for (event in events.values) {
            Marker(
                contentDescription = event.title,
                state =
                    rememberMarkerState(
                        position = LatLng(event.location.latitude, event.location.longitude)),
                title = event.title,
                tag = event.id,
                onClick = {
                  onMarkerClick(it)
                  true
                })
          }
        }
  } else {
    // Display a placeholder or loading indicator
    Box(
        modifier = Modifier.fillMaxSize().testTag("progressBar"),
        contentAlignment = Alignment.Center) {
          CircularProgressIndicator() // You can customize this part as needed
    }
  }
}
