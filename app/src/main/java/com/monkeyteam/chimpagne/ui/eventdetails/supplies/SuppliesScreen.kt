package com.monkeyteam.chimpagne.ui.eventdetails.supplies

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun SuppliesScreen(
  eventViewModel: EventViewModel,
  accountViewModel: AccountViewModel
) {
  val uiState by eventViewModel.uiState.collectAsState()
  val account by accountViewModel.uiState.collectAsState()

  val suppliesAssignedToMe = uiState.supplies.filter { it.value.assignedTo.containsKey(account.currentUserUID) }
  val nonAssignedSupplies = uiState.supplies.filter { it.value.assignedTo.isEmpty() }
  val otherSupplies = uiState.supplies - suppliesAssignedToMe - nonAssignedSupplies

  if (uiState.supplies.isEmpty()) {
    Text("No supply")
  } else {
    LazyColumn {
      if (suppliesAssignedToMe.isNotEmpty()) {
        item { Text("Supplies assigned to me") }
        items(suppliesAssignedToMe.values.toList()) {
          SupplyListElement(supply = it, eventViewModel = eventViewModel, accountViewModel = accountViewModel)
        }
      }

      if (nonAssignedSupplies.isNotEmpty()) {
        item { Text("Non assigned supplies") }
        items(nonAssignedSupplies.values.toList()) {
          SupplyListElement(supply = it, eventViewModel = eventViewModel, accountViewModel = accountViewModel)
        }
      }

      if (otherSupplies.isNotEmpty()) {
        item { Text("Other supplies") }
        items(otherSupplies.values.toList()) {
          SupplyListElement(supply = it, eventViewModel = eventViewModel, accountViewModel = accountViewModel)
        }
      }
    }
  }
}

@Composable
fun SupplyListElement(supply: ChimpagneSupply, eventViewModel: EventViewModel, accountViewModel: AccountViewModel) {

}