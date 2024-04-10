package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
  var possibleLocations by remember { mutableStateOf(emptyList<Location>()) }

  val launchSearch = {
    searching = true
    showSearchBar = false
    convertNameToLocations(
        locationQuery,
        {
          possibleLocations = it
          searching = false
          showSearchBar = true
        },
        10)
  }

  DockedSearchBar(
      query = locationQuery,
      onQueryChange = { if (!searching) locationQuery = it },
      onSearch = { launchSearch() },
      active = showSearchBar,
      onActiveChange = {},
      placeholder = { Text(stringResource(id = R.string.search_location)) },
      modifier = modifier,
      trailingIcon = {
        IconButton(onClick = launchSearch) {
          if (!searching) Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
          else CircularProgressIndicator()
        }
      }) {
        LazyColumn {
          items(possibleLocations) { location ->
            Text(
                location.name,
                Modifier.clickable {
                  locationQuery = location.name
                  showSearchBar = false
                  updateSelectedLocation(location)
                })
          }
        }
      }
}
