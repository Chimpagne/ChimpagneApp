package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.SupplyPopup
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesScreen(
  navObject: NavigationActions, eventViewModel: EventViewModel, accountViewModel: AccountViewModel
) {
  val uiState by eventViewModel.uiState.collectAsState()
  val account by accountViewModel.uiState.collectAsState()

  val suppliesAssignedToMe =
    uiState.supplies.filter { it.value.assignedTo.containsKey(account.currentUserUID) }
  val nonAssignedSupplies = uiState.supplies.filter { it.value.assignedTo.isEmpty() }
  val otherSupplies = uiState.supplies - suppliesAssignedToMe - nonAssignedSupplies

  var displayAddPopup by remember { mutableStateOf(false)  }
  if (displayAddPopup) {
    SupplyPopup(onDismissRequest = { displayAddPopup = false }) {
      eventViewModel.updateSupplyAtomically(it)
    }
  }

  var displayedSupply by remember { mutableStateOf(ChimpagneSupply()) }
  var displayAssignPopup by remember { mutableStateOf(false)  }
  if (displayAssignPopup) {
    if (eventViewModel.getRole(account.currentUserUID!!) == ChimpagneRole.GUEST) {
      GuestSupplyDialog(supply = displayedSupply,
        updateSupply = {
          eventViewModel.updateSupplyAtomically(it)
          displayAssignPopup = false
      }, userUID = account.currentUserUID!!, accounts =  hashMapOf(), onDismissRequest = {
          displayAssignPopup = false
          displayedSupply = ChimpagneSupply()
        }
      )
    } else {

    }

  }

  Scaffold(floatingActionButton = {
    if (listOf(
        ChimpagneRole.OWNER, ChimpagneRole.STAFF
      ).contains(eventViewModel.getRole(account.currentUserUID!!))
    ) {
      FloatingActionButton(onClick = { displayAddPopup = true }) {
        Icon(Icons.Default.Add, contentDescription = "Add")
      }
    }
  },
    topBar = {
      TopAppBar(title = {
        Text(
          "Supplies to bring",
//            Modifier.testTag("screen title")
        )
      }, modifier = Modifier.shadow(4.dp), navigationIcon = {
        IconButton(onClick = { navObject.goBack() }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
        }
      })
    }) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      if (uiState.supplies.isEmpty()) {
        Text("No supply")
      } else {
        LazyColumn {
          if (suppliesAssignedToMe.isNotEmpty()) {
            item { Text("Supplies assigned to me") }
            items(suppliesAssignedToMe.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                }
              )
            }
          }

          if (nonAssignedSupplies.isNotEmpty()) {
            item { Text("Non assigned supplies") }
            items(nonAssignedSupplies.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                }
              )
            }
          }

          if (otherSupplies.isNotEmpty()) {
            item { Text("Other supplies") }
            items(otherSupplies.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                }
              )
            }
          }
        }
      }
    }
  }
}