package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

@Composable
fun <T> AutoCompleteTextView(
    modifier: Modifier,
    query: String,
    queryLabel: String,
    onQueryChanged: (String) -> Unit = {},
    predictions: List<T>,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onItemClick: (T) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    itemContent: @Composable (T) -> Unit = {}
) {

  val view = LocalView.current
  val lazyListState = rememberLazyListState()
  LazyColumn(
      state = lazyListState, modifier = modifier.heightIn(max = TextFieldDefaults.MinHeight * 6)) {
        item {
          QuerySearch(
              query = query,
              label = queryLabel,
              onQueryChanged = onQueryChanged,
              onDoneActionClick = { onDoneActionClick() },
              onClearClick = {
                view.clearFocus()
                onClearClick()
              },
              onFocusChanged = onFocusChanged)
        }

        if (predictions.isNotEmpty()) {
          items(predictions) { prediction ->
            Row(Modifier.padding(8.dp).fillMaxWidth().clickable { onItemClick(prediction) }) {
              itemContent(prediction)
            }
          }
        }
      }
}

@Composable
fun QuerySearch(
    modifier: Modifier = Modifier,
    query: String,
    label: String,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onQueryChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit = {}
) {

  var showClearButton by remember { mutableStateOf(false) }

  OutlinedTextField(
      modifier =
          modifier.fillMaxWidth().onFocusChanged { focusState ->
            showClearButton = focusState.isFocused
            onFocusChanged(focusState.isFocused)
          },
      value = query,
      onValueChange = onQueryChanged,
      label = { Text(text = label) },
      textStyle = MaterialTheme.typography.bodySmall,
      singleLine = true,
      trailingIcon = {
        if (showClearButton) {
          IconButton(onClick = { onClearClick() }) {
            Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
          }
        }
      },
      keyboardActions = KeyboardActions(onDone = { onDoneActionClick() }),
      keyboardOptions =
          KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text))
}
