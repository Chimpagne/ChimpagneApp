package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun StaffSupplyDialog(supply: ChimpagneSupply, updateSupply: (ChimpagneSupply) -> Unit, userUID: ChimpagneAccountUID, accounts: Map<ChimpagneAccountUID, String>, onDismissRequest: () -> Unit) {
  CustomDialog(title = "${supply.quantity} ${supply.unit}", description = supply.description, onDismissRequest = onDismissRequest, buttonDataList = listOf(
    ButtonData("Cancel", onDismissRequest),
    if (supply.assignedTo.containsKey(userUID)) ButtonData("Unassign myself") {
      updateSupply(supply.copy(assignedTo = supply.assignedTo - userUID))
      onDismissRequest()
    }
    else ButtonData("Assign myself") {
      updateSupply(supply.copy(assignedTo = supply.assignedTo + (userUID to true)))
      onDismissRequest()
    }
  )) {
    Column(modifier = Modifier.fillMaxWidth()) {
      if (supply.assignedTo.keys.isEmpty()) {
        Text("Supply assigned to nobody !")
      } else {
        Text("Supply already assigned to")
        LazyColumn {
          supply.assignedTo.entries.forEach {(userUID, isAssigned) ->
            if (isAssigned) {
              item {
                ListItem(headlineContent = { Text(userUID) }, colors = ListItemDefaults.colors(containerColor = Color.Transparent), trailingContent = { Text("ok") })
              }
            }
          }
        }
      }
    }
  }
}