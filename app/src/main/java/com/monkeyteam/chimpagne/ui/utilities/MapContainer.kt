package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.monkeyteam.chimpagne.model.location.LocationHelper
import kotlin.math.max

@Preview(showBackground = true)
@Composable
fun MapPreview() {
  MapContainer(modifier = Modifier.fillMaxSize())
}

@Composable
fun MapContainer(
    modifier: Modifier = Modifier,
    locationHelper: LocationHelper = LocationHelper(),
    isMapInitialized: Boolean = false,
    expandBottomSheet: () -> Unit = {}
) {

  val markers by locationHelper.markers.collectAsState()

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.5196, 6.6323), 10f)
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

      // Occupy 3/4 of the screen
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
        modifier = modifier.fillMaxSize(),
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
                  expandBottomSheet()
                  true
                })
          }
        }
  } else {
    // Display a placeholder or loading indicator
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator() // You can customize this part as needed
    }
  }
}
