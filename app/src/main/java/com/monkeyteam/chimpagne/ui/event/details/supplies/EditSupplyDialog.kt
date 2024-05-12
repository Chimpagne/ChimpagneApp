package com.monkeyteam.chimpagne.ui.event.details.supplies

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun EditSupplyDialog(
  supply: ChimpagneSupply = ChimpagneSupply(),
  onDismissRequest: () -> Unit,
  onSave: (ChimpagneSupply) -> Unit,
  title: String = stringResource(id = R.string.supplies_add_dialog_title),
  saveButton: String = stringResource(id = R.string.chimpagne_add)
) {
  var description by remember { mutableStateOf(supply.description) }
  var quantity by remember { mutableStateOf(supply.quantity.toString()) }
  var unit by remember { mutableStateOf(supply.unit) }

  CustomDialog(
    title = title,
    description = stringResource(id = R.string.supplies_dialog_description),
    onDismissRequest = onDismissRequest,
    modifier = Modifier.testTag("edit_supply_dialog"),
    buttonDataList =
    listOf(
      ButtonData(
        stringResource(id = R.string.chimpagne_cancel), onClick = onDismissRequest),
      ButtonData(saveButton, modifier = Modifier.testTag("supplies_add_button")) {
        onSave(
          supply.copy(
            description = description,
            quantity = quantity.toIntOrNull() ?: 0,
            unit = unit))
        onDismissRequest()
      })) {
    Row(Modifier.fillMaxWidth()) {
      OutlinedTextField(
        modifier = Modifier.weight(0.25f).padding(5.dp).testTag("supplies_quantity_field"),
        maxLines = 1,
        value = quantity,
        onValueChange = {
          if ((it.toIntOrNull() ?: -1) > 0) {
            if (quantity == "0") {
              if (it.first() == '0') quantity = it.drop(1) else quantity = it.dropLast(1)
            } else quantity = it
          }
          if (it == "") quantity = "0"
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = KeyboardActions(onDone = { /* Handle Done action */}),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        placeholder = { Text(stringResource(id = R.string.supplies_quantity)) })
      OutlinedTextField(
        modifier = Modifier.weight(0.75f).padding(5.dp).testTag("supplies_unit_field"),
        maxLines = 1,
        value = unit,
        onValueChange = { unit = it },
        placeholder = { Text(stringResource(id = R.string.supplies_unit)) })
    }

    OutlinedTextField(
      modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("supplies_description_field"),
      maxLines = 1,
      value = description,
      onValueChange = { description = it },
      placeholder = { Text(stringResource(id = R.string.supplies_description)) })
  }
}