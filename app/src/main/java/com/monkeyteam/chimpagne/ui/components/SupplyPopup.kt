package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monkeyteam.chimpagne.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyPopup(onDismissRequest: () -> Unit, onSave: (String, Int, String) -> Unit) {
  var description by remember { mutableStateOf("") }
  var quantity by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("") }

  Dialog(
    content = {
      Column(modifier = Modifier.padding(8.dp)) {
        TextField(
          maxLines = 1,
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(id = R.string.supplies_description)) },
          modifier = Modifier.fillMaxWidth().testTag("supplies_description_field"))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
          value = quantity,
          onValueChange = { quantity = it },
          label = { Text(stringResource(id = R.string.supplies_quantity)) },
          keyboardOptions =
          KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
          keyboardActions = KeyboardActions(onDone = { /* Handle Done action */}),
          modifier = Modifier.fillMaxWidth().testTag("supplies_quantity_field"))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
          maxLines = 1,
          value = unit,
          onValueChange = { unit = it },
          label = { Text(stringResource(id = R.string.supplies_unit)) },
          modifier = Modifier.fillMaxWidth().testTag("supplies_unit_field"))
        Row {
          Button(
            onClick = { onDismissRequest() },
            modifier = Modifier.testTag("supplies_cancel_button")) {
            Text(stringResource(id = R.string.chimpagne_cancel))
          }

          Button(
            onClick = {
              onSave(description, quantity.toIntOrNull() ?: 0, unit)
              onDismissRequest()
            },
            modifier = Modifier.testTag("supplies_add_button")) {
            Text(stringResource(id = R.string.chimpagne_add))
          }
        }
      }
    },
    onDismissRequest = { onDismissRequest() })
}