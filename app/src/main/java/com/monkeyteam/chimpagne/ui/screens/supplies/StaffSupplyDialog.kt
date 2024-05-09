package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun StaffSupplyDialog(
    supply: ChimpagneSupply,
    updateSupply: (ChimpagneSupply) -> Unit,
    deleteSupply: () -> Unit,
    loggedUserUID: ChimpagneAccountUID,
    accounts: Map<ChimpagneAccountUID, ChimpagneAccount?>,
    onDismissRequest: () -> Unit
) {
  var tempSupply by remember { mutableStateOf(supply) }

  var displayDeleteSupplyDialog by remember { mutableStateOf(false) }
  if (displayDeleteSupplyDialog) {
    CustomDialog(
        title = stringResource(id = R.string.supplies_delete),
        description = stringResource(id = R.string.supplies_delete_description),
        onDismissRequest = { displayDeleteSupplyDialog = false },
        buttonDataList =
            listOf(
                ButtonData(stringResource(id = R.string.chimpagne_cancel)) {
                  displayDeleteSupplyDialog = false
                },
                ButtonData(stringResource(id = R.string.chimpagne_confirm)) {
                  deleteSupply()
                  onDismissRequest()
                }))
  }

  var displayEditSupplyDialog by remember { mutableStateOf(false) }
  if (displayEditSupplyDialog) {
    EditSupplyDialog(
        supply = tempSupply,
        onDismissRequest = { displayEditSupplyDialog = false },
        onSave = { tempSupply = it })
  }

  CustomDialog(
      title = "${tempSupply.quantity} ${tempSupply.unit}",
      description = tempSupply.description,
      onDismissRequest = onDismissRequest,
      buttonDataList =
          listOf(
              ButtonData(stringResource(id = R.string.chimpagne_cancel), onDismissRequest),
              ButtonData(stringResource(id = R.string.chimpagne_delete), { displayDeleteSupplyDialog = true }),
              ButtonData(stringResource(id = R.string.chimpagne_edit)) {
                displayEditSupplyDialog = true
              },
              ButtonData(stringResource(id = R.string.chimpagne_save)) {
                displayEditSupplyDialog = false
                updateSupply(tempSupply)
                onDismissRequest()
              })) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
          if (tempSupply.assignedTo.keys.isEmpty()) {
            item { Text(stringResource(id = R.string.supplies_supply_not_assigned)) }
          } else {
            item { Text(stringResource(id = R.string.supplies_supply_assigned_to)) }
            accounts.entries.forEach { (userUID, userAccount) ->
              if (tempSupply.assignedTo[userUID] == true) {
                item {
                  SupplyDialogAccountEntry(
                      account = userAccount,
                      loggedUserUID = loggedUserUID,
                      showCheckBox = true,
                      true) {
                        tempSupply = tempSupply.copy(assignedTo = tempSupply.assignedTo - userUID)
                      }
                }
              }
            }
          }
          if (!tempSupply.assignedTo.keys.containsAll(accounts.keys)) {
            item { Text(stringResource(id = R.string.supplies_not_assigned_to)) }
            accounts.entries.forEach { (userUID, userAccount) ->
              if (tempSupply.assignedTo[userUID] != true) {
                item {
                  SupplyDialogAccountEntry(
                      account = userAccount,
                      loggedUserUID = loggedUserUID,
                      showCheckBox = true,
                      false) {
                        tempSupply =
                            tempSupply.copy(assignedTo = tempSupply.assignedTo + (userUID to true))
                      }
                }
              }
            }
          }
        }
      }
}
