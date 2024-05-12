package com.monkeyteam.chimpagne.ui.event.details

import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesScreen(
  navObject: NavigationActions,
  eventViewModel: EventViewModel,
  accountViewModel: AccountViewModel
) {
  val eventUiState by eventViewModel.uiState.collectAsState()
  val accountsUiState by accountViewModel.uiState.collectAsState()

  val suppliesAssignedToMe =
    eventUiState.supplies.filter {
      it.value.assignedTo.containsKey(accountsUiState.currentUserUID!!)
    }
  val nonAssignedSupplies = eventUiState.supplies.filter { it.value.assignedTo.isEmpty() }
  val otherSupplies =
    eventUiState.supplies.filter {
      it.value.assignedTo.isNotEmpty() &&
              !it.value.assignedTo.containsKey(accountsUiState.currentUserUID)
    }

  var displayAddPopup by remember { mutableStateOf(false) }
  if (displayAddPopup) {

//    EditSupplyDialog(
//      supply = ChimpagneSupply(),
//      onDismissRequest = { displayAddPopup = false },
//      onSave = { eventViewModel.updateSupplyAtomically(it) })
  }

  var displayedSupply by remember { mutableStateOf(ChimpagneSupply()) }
  var displayAssignPopup by remember { mutableStateOf(false) }
  if (displayAssignPopup) {
    if (eventUiState.currentUserRole == ChimpagneRole.GUEST) {
      GuestSupplyDialog(
        supply = displayedSupply,
        assignMyself = {
          if (it) {
            eventViewModel.assignSupplyAtomically(
              displayedSupply.id, accountViewModelState.currentUserUID!!)
          } else {
            eventViewModel.unassignSupplyAtomically(
              displayedSupply.id, accountViewModelState.currentUserUID!!)
          }
          displayAssignPopup = false
          displayedSupply = ChimpagneSupply()
        },
        loggedUserUID = accountViewModelState.currentUserUID!!,
        accounts = accountViewModelState.fetchedAccounts,
        onDismissRequest = {
          displayAssignPopup = false
          displayedSupply = ChimpagneSupply()
        })
    } else {
      StaffSupplyDialog(
        supply = displayedSupply,
        updateSupply = { eventViewModel.updateSupplyAtomically(it) },
        deleteSupply = { eventViewModel.removeSupplyAtomically(displayedSupply.id) },
        loggedUserUID = accountViewModelState.currentUserUID!!,
        accounts = accountViewModelState.fetchedAccounts,
        onDismissRequest = {
          displayAssignPopup = false
          displayedSupply = ChimpagneSupply()
        })
    }
  }

  Scaffold(
    floatingActionButton = {
      if (listOf(ChimpagneRole.OWNER, ChimpagneRole.STAFF).contains(eve.currentUserRole)) {
        FloatingActionButton(
          onClick = { displayAddPopup = true }, modifier = Modifier.testTag("supply_add")) {
          Icon(Icons.Default.Add, contentDescription = "Add")
        }
      }
    },
    topBar = {
      TopAppBar(
        title = { Text(stringResource(id = R.string.supplies_screen_title)) },
        modifier = Modifier.shadow(4.dp),
        navigationIcon = {
          IconButton(onClick = { navObject.goBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
          }
        })
    }) { innerPadding ->
    if (uiState.supplies.isEmpty()) {
      Text(
        text = stringResource(id = R.string.supplies_empty),
        modifier =
        Modifier.fillMaxWidth()
          .fillMaxHeight()
          .padding(innerPadding)
          .wrapContentHeight(align = Alignment.CenterVertically)
          .testTag("supply_nothing"),
        textAlign = TextAlign.Center)
    } else {
      Column(Modifier.padding(innerPadding)) {
        LazyColumn {
          if (suppliesAssignedToMe.isNotEmpty()) {
            item {
              Text(
                stringResource(id = R.string.supplies_supply_assigned_to_you),
                modifier = Modifier.padding(12.dp, 8.dp))
            }
            items(suppliesAssignedToMe.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                })
            }
          }

          if (nonAssignedSupplies.isNotEmpty()) {
            item {
              Text(
                stringResource(id = R.string.supplies_not_assigned_to_anyone),
                modifier = Modifier.padding(12.dp, 8.dp).testTag("supply_not_assigned"))
            }
            items(nonAssignedSupplies.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                },
                modifier = Modifier.testTag("supply_card"))
            }
          }

          if (otherSupplies.isNotEmpty()) {
            item {
              Text(
                stringResource(id = R.string.supplies_already_assigned),
                modifier = Modifier.padding(12.dp, 8.dp))
            }
            items(otherSupplies.values.toList()) {
              SupplyCard(
                supply = it,
                onClick = {
                  displayAssignPopup = true
                  displayedSupply = it
                })
            }
          }
        }
      }
    }
  }
}