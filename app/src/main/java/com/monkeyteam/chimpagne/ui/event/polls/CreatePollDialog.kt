package com.monkeyteam.chimpagne.ui.event.polls

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun CreatePollDialog(
    onPollCreate: (ChimpagnePoll) -> Unit,
    onPollCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
  var title by remember { mutableStateOf("") }
  var query by remember { mutableStateOf("") }
  val options = remember { mutableStateListOf("", "") }
  val context = LocalContext.current

  CustomDialog(
      title = stringResource(id = R.string.polls_create_a_poll_title),
      onDismissRequest = onDismissRequest,
      buttonDataList =
          listOf(
              ButtonData(
                  text = stringResource(id = R.string.chimpagne_cancel),
                  modifier = Modifier.testTag("cancel poll button"),
                  onClick = onPollCancel),
              ButtonData(
                  text = stringResource(id = R.string.chimpagne_confirm),
                  modifier = Modifier.testTag("confirm poll button"),
                  onClick = {
                    if (title == "" || query == "" || options.contains("")) {
                      Toast.makeText(
                              context,
                              context.getString(R.string.polls_create_a_poll_empty_field_toast),
                              Toast.LENGTH_SHORT)
                          .show()
                    } else {
                      onPollCreate(ChimpagnePoll(title = title, query = query, options = options))
                    }
                  }))) {
        Column(modifier = Modifier.heightIn(0.dp, 400.dp)) {
          LazyColumn {
            item {
              OutlinedTextField(
                  modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("poll title field"),
                  value = title,
                  onValueChange = { title = it },
                  label = { Text(stringResource(id = R.string.polls_create_a_poll_title_field)) })
              OutlinedTextField(
                  modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("poll query field"),
                  value = query,
                  onValueChange = { query = it },
                  label = { Text(stringResource(id = R.string.polls_create_a_poll_query_field)) })
            }
            items(options.indices.toList()) { id ->
              var optionMutable by remember { mutableStateOf(options[id]) }
              OutlinedTextField(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(5.dp)
                          .testTag("poll option " + (id + 1) + " field"),
                  value = optionMutable,
                  onValueChange = {
                    optionMutable = it
                    options[id] = it
                  },
                  label = {
                    Text(
                        stringResource(id = R.string.polls_create_a_poll_option_field) +
                            " " +
                            (id + 1))
                  },
                  trailingIcon = {
                    if (options.size > 2 && id == options.size - 1)
                        Icon(
                            Icons.Rounded.RemoveCircle,
                            "remove option button",
                            Modifier.clickable { options.removeLast() })
                  })
            }
            item {
              Row {
                if (options.size < 4) {
                  Icon(
                      Icons.Rounded.AddCircle,
                      "add option button",
                      Modifier.clickable { options.add("") }.padding(5.dp))
                }
              }
            }
          }
        }
      }
}
