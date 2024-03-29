package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
<<<<<<< HEAD
import androidx.compose.runtime.collectAsState
=======
>>>>>>> main
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
<<<<<<< HEAD
=======
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
>>>>>>> main
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
<<<<<<< HEAD
import com.monkeyteam.chimpagne.model.location.LocationHelper

@Preview(showBackground = true)
@Composable
fun MapPreview() {
  MapContainer(modifier = Modifier.fillMaxSize())
}

@Composable
fun MapContainer(modifier: Modifier = Modifier, locationHelper: LocationHelper = LocationHelper()) {

  val markers by locationHelper.markers.collectAsState()
  val latMap by remember { mutableDoubleStateOf(46.5196) }
  val lonMap by remember { mutableDoubleStateOf(6.6323) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(latMap, lonMap), 10f)
  }

  GoogleMap(
      cameraPositionState = cameraPositionState,
      modifier = modifier.fillMaxSize(),
      uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
        for (marker in markers) {
          Marker(
              state = rememberMarkerState(position = LatLng(marker.latitude, marker.longitude)),
              title = marker.name)
        }
      }
=======
import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapContainer {

  private val _markers = MutableLiveData<List<Location>>()
  val markers: LiveData<List<Location>> = _markers

  suspend fun addMarker(location: Location) =
      withContext(Dispatchers.Main) {
        val currentMarkers = _markers.value.orEmpty().toMutableList()
        currentMarkers.add(location)
        _markers.value = currentMarkers
      }

  @Preview
  @Composable
  fun Map() {
    val latMap by remember { mutableDoubleStateOf(46.5196) }
    val lonMap by remember { mutableDoubleStateOf(6.6323) }

    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(LatLng(latMap, lonMap), 10f)
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize(),
        uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
          for (marker in markers.value.orEmpty()) {
            Marker(
                state = rememberMarkerState(position = LatLng(marker.latitude, marker.longitude)),
                title = marker.name)
          }
        }
  }
>>>>>>> main
}
