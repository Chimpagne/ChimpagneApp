package com.monkeyteam.chimpagne.ui.event.details.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
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
      //      GuestSupplyDialog(
      //        supply = displayedSupply,
      //        assignMyself = {
      //          if (it) {
      //            eventViewModel.assignSupplyAtomically(
      //              displayedSupply.id, accountViewModelState.currentUserUID!!)
      //          } else {
      //            eventViewModel.unassignSupplyAtomically(
      //              displayedSupply.id, accountViewModelState.currentUserUID!!)
      //          }
      //          displayAssignPopup = false
      //          displayedSupply = ChimpagneSupply()
      //        },
      //        loggedUserUID = accountViewModelState.currentUserUID!!,
      //        accounts = accountViewModelState.fetchedAccounts,
      //        onDismissRequest = {
      //          displayAssignPopup = false
      //          displayedSupply = ChimpagneSupply()
      //        })
    } else {
      //      StaffSupplyDialog(
      //        supply = displayedSupply,
      //        updateSupply = { eventViewModel.updateSupplyAtomically(it) },
      //        deleteSupply = { eventViewModel.removeSupplyAtomically(displayedSupply.id) },
      //        loggedUserUID = accountViewModelState.currentUserUID!!,
      //        accounts = accountViewModelState.fetchedAccounts,
      //        onDismissRequest = {
      //          displayAssignPopup = false
      //          displayedSupply = ChimpagneSupply()
      //        })
    }
  }

  @Composable
  fun DisplaySupplyListIfNotEmpty(listTitle: String, supplyList: List<ChimpagneSupply>) {
    if (supplyList.isNotEmpty()) {
      Text(text = listTitle, modifier = Modifier.padding(12.dp, 8.dp))
      LazyColumn {
        supplyList.forEach { supply ->
          item {
            SupplyCard(supply = supply) {
              displayedSupply = supply
              displayAssignPopup = true
            }
          }
        }
      }
    }
  }

  Scaffold(
      floatingActionButton = {
        if (listOf(ChimpagneRole.OWNER, ChimpagneRole.STAFF)
            .contains(eventUiState.currentUserRole)) {
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
        if (eventUiState.supplies.isEmpty()) {
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
            DisplaySupplyListIfNotEmpty(
                listTitle = stringResource(id = R.string.supplies_supply_assigned_to_you),
                supplyList = suppliesAssignedToMe.values.toList())
            DisplaySupplyListIfNotEmpty(
                listTitle = stringResource(id = R.string.supplies_not_assigned_to_anyone),
                supplyList = nonAssignedSupplies.values.toList())
            DisplaySupplyListIfNotEmpty(
                listTitle = stringResource(id = R.string.supplies_already_assigned),
                supplyList = otherSupplies.values.toList())
          }
        }
      }
}
