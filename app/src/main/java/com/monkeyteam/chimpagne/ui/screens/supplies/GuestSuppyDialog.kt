package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun GuestSupplyDialog(
    supply: ChimpagneSupply,
    assignMyself: (Boolean) -> Unit,
    loggedUserUID: ChimpagneAccountUID,
    accounts: Map<ChimpagneAccountUID, ChimpagneAccount?>,
    onDismissRequest: () -> Unit
) {
  CustomDialog(
      title = "${supply.quantity} ${supply.unit}",
      description = supply.description,
      onDismissRequest = onDismissRequest,
      modifier = Modifier.testTag("edit_supply_dialog"),
      buttonDataList =
          listOf(
              ButtonData(
                  stringResource(id = R.string.chimpagne_cancel), onClick = onDismissRequest),
              if (supply.assignedTo.containsKey(loggedUserUID))
                  ButtonData(stringResource(id = R.string.supplies_unassign_myself)) {
                    assignMyself(false)
                    onDismissRequest()
                  }
              else
                  ButtonData(stringResource(id = R.string.supplies_assign_myself)) {
                    assignMyself(true)
                    onDismissRequest()
                  })) {
        Column(modifier = Modifier.fillMaxWidth()) {
          if (supply.assignedTo.keys.isEmpty()) {
            Text(stringResource(id = R.string.supplies_supply_not_assigned))
          } else {
            Text(stringResource(id = R.string.supplies_supply_assigned_to))
            LazyColumn {
              supply.assignedTo.entries.forEach { (userUID, isAssigned) ->
                if (isAssigned) {
                  item {
                    SupplyDialogAccountEntry(
                        account = accounts[userUID], loggedUserUID = loggedUserUID)
                  }
                }
              }
            }
          }
        }
      }
}
