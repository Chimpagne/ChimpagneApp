package com.monkeyteam.chimpagne.ui.event.polls

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.model.database.ChimpagnePollId
import com.monkeyteam.chimpagne.model.database.ChimpagnePollOptionListIndex
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun VotePollDialog(
    poll: ChimpagnePoll,
    userRole: ChimpagneRole,
    onOptionVote: (ChimpagnePollOptionListIndex) -> Unit,
    onPollDelete: (ChimpagnePollId) -> Unit,
    onPollCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
  var hasSelectedOption by remember { mutableStateOf(false) }
  var selectedOptionId by remember { mutableIntStateOf(0) }
  val context = LocalContext.current

  CustomDialog(
      title = poll.query,
      onDismissRequest = onDismissRequest,
      buttonDataList =
          listOf(
              ButtonData(
                  text = stringResource(id = R.string.chimpagne_cancel),
                  modifier = Modifier.testTag("cancel option button"),
                  onClick = onPollCancel)) +
              if (listOf(ChimpagneRole.OWNER, ChimpagneRole.STAFF).contains(userRole)) {
                listOf(
                    ButtonData(
                        text = stringResource(id = R.string.chimpagne_delete),
                        modifier = Modifier.testTag("delete poll button"),
                        onClick = { onPollDelete(poll.id) }))
              } else {
                emptyList()
              } +
              listOf(
                  ButtonData(
                      text = stringResource(id = R.string.chimpagne_confirm),
                      modifier = Modifier.testTag("confirm option button"),
                      onClick = {
                        if (!hasSelectedOption) {
                          Toast.makeText(
                                  context, "You have not selected an option", Toast.LENGTH_SHORT)
                              .show()
                        } else {
                          onOptionVote(selectedOptionId)
                        }
                      }))) {
        Column(modifier = Modifier.heightIn(0.dp, 200.dp)) {
          LazyColumn {
            items(poll.options.indices.toList()) { id ->
              ListItem(
                  headlineContent = { Text(text = poll.options[id]) },
                  colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                  trailingContent = {
                    if (id == selectedOptionId && hasSelectedOption) {
                      Icon(
                          imageVector = Icons.Rounded.RadioButtonChecked,
                          contentDescription = "option " + (id + 1) + " selected")
                    } else {
                      Icon(
                          imageVector = Icons.Rounded.RadioButtonUnchecked,
                          contentDescription = "option " + (id + 1) + " unselected",
                          modifier =
                              Modifier.clickable {
                                hasSelectedOption = true
                                selectedOptionId = id
                              })
                    }
                  })
            }
          }
        }
      }
}
