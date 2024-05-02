package com.monkeyteam.chimpagne.ui.eventdetails.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun StaffSupplyDialog(supply: ChimpagneSupply, updateSupply: (ChimpagneSupply) -> Unit, userUID: ChimpagneAccountUID, event: ChimpagneEvent, onDismissRequest: () -> Unit) {
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

//@Preview
//@Composable
//fun banana() {
//  var showSupplyDialog by remember { mutableStateOf( true) }
//  if (showSupplyDialog) SupplyGuestDialog(ChimpagneSupply("banana", "At migros", 5, "bananas", hashMapOf("Monkey" to true, "Juan" to true, "Jean" to false)), {
//    showSupplyDialog = false
//  }, "Monkey", {
//    showSupplyDialog = false
//  })
//}