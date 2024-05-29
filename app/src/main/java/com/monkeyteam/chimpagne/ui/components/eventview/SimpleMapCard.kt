package com.monkeyteam.chimpagne.ui.components.eventview

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.utilities.getZoomLevel


@Composable
fun SimpleMapCard(startingPosition: Location) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    val onClick: () -> Unit = {
            val gmmIntentUri = Uri.parse("google.navigation:q=${startingPosition.latitude},${startingPosition.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)

    }

    LaunchedEffect(startingPosition) {
            val zoomLevel = getZoomLevel(2000.0)
            val cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(startingPosition.latitude, startingPosition.longitude), zoomLevel)
            cameraPositionState.move(cameraUpdate)

    }

    Box(modifier = Modifier
        .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        .fillMaxWidth()
        .pointerInput(Unit) {
            detectTapGestures { onClick() }
        }) {
        Column {
            Card(modifier = Modifier
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .aspectRatio(1.7f)
                .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                GoogleMap(
                    modifier = Modifier
                        .testTag("map card")
                        .fillMaxSize(),
                    onMapClick = {onClick()},
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = false,
                        scrollGesturesEnabled = false,
                        scrollGesturesEnabledDuringRotateOrZoom = false,
                        tiltGesturesEnabled = false,
                        zoomGesturesEnabled = false,
                        rotationGesturesEnabled = false
                    ),
                    properties = MapProperties(
                        isMyLocationEnabled = false,
                        isTrafficEnabled = false,
                        isIndoorEnabled = true
                    )
                ) {
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 8.dp).height(20.dp)
                )
                Text(
                    text = startingPosition.name,
                    style = ChimpagneTypography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}