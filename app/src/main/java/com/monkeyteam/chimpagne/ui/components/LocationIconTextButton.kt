package com.monkeyteam.chimpagne.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.location.LocationState
import com.monkeyteam.chimpagne.ui.theme.CustomGreen
import com.monkeyteam.chimpagne.ui.theme.CustomOrange

@Composable
fun LocationIconTextButton(
    locationState: LocationState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  val text =
      when (locationState) {
        is LocationState.Set -> stringResource(id = R.string.find_event_location_set)
        is LocationState.Searching ->
            stringResource(id = R.string.find_event_event_locate_searching)
        else -> stringResource(id = R.string.activate_gps)
      }

  val icon =
      when (locationState) {
        is LocationState.Set -> Icons.Rounded.CheckCircle
        is LocationState.Searching -> Icons.Default.HourglassEmpty
        else -> Icons.Rounded.MyLocation
      }

  val color =
      when (locationState) {
        is LocationState.Set -> CustomGreen
        is LocationState.Searching -> CustomOrange
        else -> MaterialTheme.colorScheme.surfaceVariant
      }

  IconTextButton(text = text, icon = icon, color = color, onClick = onClick, modifier = modifier)
}
