package com.monkeyteam.chimpagne.ui.eventdetails.supplies

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun SupplyGuestDialog(supply: ChimpagneSupply, updateSupply: (ChimpagneSupply) -> Unit, assignedToMyself: Boolean = false, onDismissRequest: () -> Unit) {

  CustomDialog(title = "${supply.quantity} ${supply.unit}", description = supply.description, onDismissRequest = onDismissRequest, buttonDataList = listOf(
    ButtonData("Cancel", onDismissRequest),
    if (assignedToMyself) ButtonData("Unassign myself", onDismissRequest)
    else ButtonData("Assign myself", onDismissRequest)
  )) {
    if (supply.assignedTo.keys.isEmpty()) {
      Text("Supply assigned to nobody !")
    } else {
      Text("Supply already assigned to")
      LazyColumn {
        supply.assignedTo.entries.forEach {(userUID, isAssigned) ->
          if (isAssigned) {
            item { 
              Text(text = userUID)
            }
          }
        }
      }
    }
  }

}

@Preview
@Composable
fun monkey() {
  var showSupplyDialog by remember { mutableStateOf( true) }
  if (showSupplyDialog) SupplyGuestDialog(ChimpagneSupply("banana", "At migros", 5, "bananas"), {
    showSupplyDialog = false
  }, false, {
    showSupplyDialog = false
  })
}