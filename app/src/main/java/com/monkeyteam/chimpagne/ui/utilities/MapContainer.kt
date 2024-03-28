package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.monkeyteam.chimpagne.model.location.LocationHelper

@Preview(showBackground = true)
@Composable
fun MapPreview() {
  MapContainer(modifier = Modifier.fillMaxSize())
}

@Composable
fun MapContainer(modifier: Modifier, locationHelper: LocationHelper = LocationHelper()) {
  modifier.fillMaxSize()

  val markers by locationHelper.markers.collectAsState()
  val latMap by remember { mutableDoubleStateOf(46.5196) }
  val lonMap by remember { mutableDoubleStateOf(6.6323) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(latMap, lonMap), 10f)
  }

  GoogleMap(
      cameraPositionState = cameraPositionState,
      modifier = modifier,
      uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
        for (marker in markers) {
          Marker(
              state = rememberMarkerState(position = LatLng(marker.latitude, marker.longitude)),
              title = marker.name)
        }
      }
}
