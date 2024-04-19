package com.monkeyteam.chimpagne.ui.utilities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.ChimpagneClustering
import kotlin.math.ln

// Function to calculate zoom level from radius in meters
fun getZoomLevel(radius: Double): Float {
  val scale = (radius + radius / 2) / 400
  return (16 - ln(scale) / ln(2.0)).toFloat()
}

@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun MapContainer(
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    isMapInitialized: Boolean = false,
    bottomSheetState: SheetState,
    onMarkerClick: (MarkerData) -> Unit,
    events: Map<String, ChimpagneEvent>,
    radius: Double,
    startingPosition: Location?,
) {

  val dynamicBottomPadding =
      when (bottomSheetState.targetValue) {
        SheetValue.Expanded -> 180.dp
        SheetValue.PartiallyExpanded -> 0.dp
        SheetValue.Hidden -> 0.dp
      }

  LaunchedEffect(events) {
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
        modifier =
            Modifier.testTag("ggle_maps").fillMaxSize().padding(bottom = dynamicBottomPadding),
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
                cameraPositionState.move(CameraUpdateFactory.zoomIn())
                false
              },
              onClusterItemClick = { e ->
                onMarkerClick(e)
                true
              })
        }
  } else {
    // Display a placeholder or loading indicator
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

  fun getMarkerId(): String {
    return id
  }
}

@Composable
fun IconAsClusterContentItem(data: MarkerData) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = if (data.name.length > 16) data.name.substring(0, 13) + "..." else data.name,
        color = MaterialTheme.colorScheme.tertiary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp)
    Icon(
        modifier = Modifier.size(32.dp),
        imageVector = Icons.Rounded.LocationOn,
        contentDescription = "custum icon for cluster item",
        tint = Color.Red)
  }
}
