package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun FindEventSearchBar() {
  var query by remember { mutableStateOf("") }
  var isActive by remember { mutableStateOf(false) }

  SearchBar(
      query = query,
      onQueryChange = { query = it },
      onSearch = { /* Handle search */},
      active = isActive,
      onActiveChange = { isActive = it },
      modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp),
      leadingIcon = {
        if (isActive) {
          IconButton(onClick = { isActive = false }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        }
      },
      trailingIcon = {
        if (isActive) {
          if (query.isNotEmpty()) {
            IconButton(onClick = { query = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          } else {
            Icon(Icons.Default.Search, contentDescription = "Search")
          }
        } else {
          IconButton(onClick = { isActive = true }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
          }
        }
      },
      placeholder = { Text("Search events") }) {}
}
