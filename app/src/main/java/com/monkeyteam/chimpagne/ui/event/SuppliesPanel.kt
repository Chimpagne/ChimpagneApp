package com.monkeyteam.chimpagne.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.event.details.supplies.EditSupplyDialog
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  Column(modifier = Modifier.padding(16.dp)) {
    Legend(
        stringResource(id = R.string.event_creation_screen_supplies),
        Icons.Rounded.Inventory2,
        "supplies_title")
    Spacer(modifier = Modifier.height(16.dp))

    val showAddDialog = remember { mutableStateOf(false) }
    Button(
        onClick = { showAddDialog.value = true },
        modifier = Modifier.testTag("add_supplies_button")) {
          Text(stringResource(id = R.string.event_creation_screen_add_supplies))
        }

    if (showAddDialog.value) {

      EditSupplyDialog(onDismissRequest = { showAddDialog.value = false }, onSave = eventViewModel::addSupply)
    }

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn {
      items(uiState.supplies.values.toList()) { item ->
        ListItem(
            headlineContent = {
              Text(
                  text = item.description,
              )
            },
            supportingContent = {
              Text(text = item.quantity.toString() + " " + item.unit, color = Color.Gray)
            },
            trailingContent = {
              IconButton(
                  onClick = { eventViewModel.removeSupply(item.id) },
                  modifier = Modifier.testTag(item.description),
                  content = {
                    Icon(
                        active = true,
                        activeContent = {
                          androidx.compose.material3.Icon(
                              imageVector = Icons.Default.Cancel, contentDescription = "Delete")
                        },
                        inactiveContent = {
                          androidx.compose.material3.Icon(
                              imageVector = Icons.Default.Cancel, contentDescription = "Delete")
                        })
                  })
            })
      }
    }
  }
}
