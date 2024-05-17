package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import kotlin.math.ln

// Function to calculate zoom level from radius in meters
fun getZoomLevel(radius: Double): Float {
  val scale = (radius + radius / 2) / 400
  return (16 - ln(scale) / ln(2.0)).toFloat()
}

@OptIn(MapsComposeExperimentalApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MapContainer(
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    isMapInitialized: Boolean = false,
    onMarkerClick: (Cluster<MarkerData>) -> Unit,
    events: Map<String, ChimpagneEvent>,
    radius: Double,
    startingPosition: Location?,
    closeBottomSheet: () -> Unit = {},
) {

  LaunchedEffect(events, radius, startingPosition) {
    if (events.isNotEmpty() && startingPosition != null) {

      val zoomLevel = getZoomLevel(radius)
      val cameraUpdate =
          CameraUpdateFactory.newLatLngZoom(
              LatLng(startingPosition.latitude, startingPosition.longitude), zoomLevel)
      cameraPositionState.move(cameraUpdate)
    }
  }

  if (isMapInitialized) {

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.testTag("ggle_maps").fillMaxSize(),
        onMapClick = { closeBottomSheet() },
        uiSettings =
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false)) {
          if (startingPosition != null) {
            Circle(
                center = LatLng(startingPosition.latitude, startingPosition.longitude),
                radius = radius,
                strokeColor = Color.Red,
                strokeWidth = 2f,
                fillColor = Color(0x11FF0000) // Semi-transparent red
                )
          }

          val markersData =
              listOf(events.map { (id, event) -> MarkerData(id, event.title, event.location) })
                  .flatten()

          ChimpagneClustering(
              items = markersData,
              onClusterClick = {
                onMarkerClick(it)
                true
              },
              onClusterItemClick = { markerData ->
                onMarkerClick(SingletonCluster(markerData))
                true
              })
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

data class MarkerData(val id: String, val name: String, val location: Location) : ClusterItem {
  override fun getPosition(): LatLng {
    return LatLng(location.latitude, location.longitude)
  }

  override fun getTitle(): String {
    return name
  }

  override fun getSnippet(): String {
    return ""
  }

  override fun getZIndex(): Float {
    return 1f
  }
}
