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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapContainer(
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    mapViewModel: MapViewModel = MapViewModel(),
    isMapInitialized: Boolean = false,
    bottomSheetState: SheetState,
    onMarkerClick: (Marker) -> Unit,
) {

  val markers by mapViewModel.markers.collectAsState()

  val dynamicBottomPadding =
      when (bottomSheetState.targetValue) {
        SheetValue.Expanded -> 300.dp
        SheetValue.PartiallyExpanded -> 0.dp // Adjust as needed for the partially expanded state
        SheetValue.Hidden -> 0.dp
      }

  LaunchedEffect(markers) {
    if (markers.isNotEmpty()) {
      val latitudes = markers.map { it.latitude }
      val longitudes = markers.map { it.longitude }
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
        modifier = Modifier.fillMaxSize().padding(bottom = dynamicBottomPadding),
        uiSettings =
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false)) {
          for (marker in markers) {
            Marker(
                state = rememberMarkerState(position = LatLng(marker.latitude, marker.longitude)),
                title = marker.name,
                onClick = {
                  onMarkerClick(it)
                  true
                })
          }
        }
  } else {
    // Display a placeholder or loading indicator
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator() // You can customize this part as needed
    }
  }
}
