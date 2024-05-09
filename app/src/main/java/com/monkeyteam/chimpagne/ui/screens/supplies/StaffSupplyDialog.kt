package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun StaffSupplyDialog(
  supply: ChimpagneSupply,
  updateSupply: (ChimpagneSupply) -> Unit,
  userUID: ChimpagneAccountUID,
  accounts: Map<ChimpagneAccountUID, ChimpagneAccount?>,
  onDismissRequest: () -> Unit
) {
  CustomDialog(
    title = "${supply.quantity} ${supply.unit}",
    description = supply.description,
    onDismissRequest = onDismissRequest,
    buttonDataList = listOf(
      ButtonData("Cancel", onDismissRequest),
      ButtonData("Edit", {}),
      ButtonData("Save", {})
    )
  ) {
    var tempSupply by remember { mutableStateOf(supply) }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
      if (tempSupply.assignedTo.keys.isEmpty()) {
        item { Text("Supply assigned to nobody !") }
      } else {
        item { Text("Supply assigned to") }
        accounts.entries.forEach { (userUID, userAccount) ->
          if (tempSupply.assignedTo[userUID] == true) {
            item {
              SupplyDialogAccountEntry(account = userAccount, showCheckBox = true, true) {
                tempSupply = tempSupply.copy(assignedTo = tempSupply.assignedTo - userUID)
              }
            }
          }
        }
      }
      if (!tempSupply.assignedTo.keys.containsAll(accounts.keys)) {
        item { Text("Supply assigned to") }
        accounts.entries.forEach { (userUID, userAccount) ->
          if (tempSupply.assignedTo[userUID] != true) {
            item {
              SupplyDialogAccountEntry(account = userAccount, showCheckBox = true, false) {
                tempSupply = tempSupply.copy(assignedTo = tempSupply.assignedTo + (userUID to true))
              }
            }
          }
        }
      }
    }
  }
}