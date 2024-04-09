package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.monkeyteam.chimpagne.model.location.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFinder(
    locationQuery: String,
    onLocationQueryChange: (String) -> Unit,
    possibleLocations: List<Location>,
    selectLocation: (Location) -> Unit,
    modifier: Modifier = Modifier
) {

  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded },
      modifier = modifier.testTag("locationDropDownMenuBox")) {
        TextField(
            value = locationQuery,
            onValueChange = onLocationQueryChange,
            label = { Text("Location") },
            placeholder = { Text("Enter an address") },
            modifier =
                Modifier.testTag("inputEventLocation")
                    .menuAnchor()
                    .fillMaxWidth()
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceVariant))
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {},
            modifier = Modifier.testTag("locationFieldDropDown")) {
              possibleLocations.forEach { location ->
                DropdownMenuItem(
                    onClick = {
                      selectLocation(location)
                      expanded = false
                    },
                    text = { Text(text = location.name) })
              }
            }
      }
}
