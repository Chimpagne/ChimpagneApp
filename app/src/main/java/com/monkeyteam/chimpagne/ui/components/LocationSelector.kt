package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.convertNameToLocations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelector(
    selectedLocation: Location?,
    updateSelectedLocation: (Location) -> Unit,
    modifier: Modifier = Modifier
) {

  var locationQuery by remember { mutableStateOf(selectedLocation?.name ?: "") }
  var searching by remember { mutableStateOf(false) }
  var showSearchBar by remember { mutableStateOf(false) }
  var searchCompleted by remember { mutableStateOf(false) }
  var possibleLocations by remember { mutableStateOf(emptyList<Location>()) }

  val launchSearch = {
    searching = true
    showSearchBar = true
    convertNameToLocations(
        locationQuery,
        {
          possibleLocations = it
          searching = false
          searchCompleted = it.isNotEmpty()
        },
        10)
  }

  val clearLocationInput = {
    locationQuery = ""
    possibleLocations = emptyList()
    searchCompleted = false
    updateSelectedLocation(Location())
  }

  DockedSearchBar(
      query = locationQuery,
      onQueryChange = {
        locationQuery = it
        searchCompleted = false
      },
      onSearch = { launchSearch() },
      active = showSearchBar,
      onActiveChange = { showSearchBar = it },
      placeholder = { Text(stringResource(id = R.string.search_location)) },
      modifier = modifier.testTag("LocationComponent"),
      trailingIcon = {
        when {
          searching -> CircularProgressIndicator()
          locationQuery.isNotEmpty() && searchCompleted ->
              IconButton(onClick = clearLocationInput) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
              }
          locationQuery.isNotEmpty() && !searchCompleted ->
              IconButton(onClick = launchSearch) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
              }
        }
      }) {
        if (possibleLocations.isNotEmpty()) {
          Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp), // Set a rounded corner shape
              color = MaterialTheme.colorScheme.surface) {
                LazyColumn {
                  items(possibleLocations) { location ->
                    Text(
                        text = location.name,
                        modifier =
                            Modifier.clickable {
                                  locationQuery = location.name
                                  searchCompleted = true
                                  showSearchBar = false
                                  updateSelectedLocation(location)
                                }
                                .fillMaxWidth()
                                .padding(16.dp))
                    HorizontalDivider(color = Color.LightGray)
                  }
                }
              }
        }
      }
}
