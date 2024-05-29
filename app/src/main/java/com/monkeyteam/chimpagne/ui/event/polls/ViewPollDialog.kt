package com.monkeyteam.chimpagne.ui.event.polls

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.model.database.ChimpagnePollId
import com.monkeyteam.chimpagne.model.database.ChimpagnePollOptionListIndex
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog

@Composable
fun ViewPollDialog(
    poll: ChimpagnePoll,
    selectedOptionId: ChimpagnePollOptionListIndex,
    userRole: ChimpagneRole,
    onPollDelete: (ChimpagnePollId) -> Unit,
    onPollCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
  CustomDialog(
      title = poll.query,
      onDismissRequest = onDismissRequest,
      buttonDataList =
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
                      text = stringResource(id = R.string.chimpagne_return),
                      modifier = Modifier.testTag("return button"),
                      onClick = onPollCancel))) {
        Column(modifier = Modifier.heightIn(0.dp, 200.dp)) {
          LazyColumn {
            items(poll.options.indices.toList()) { id ->
              ListItem(
                  headlineContent = { Text(text = poll.options[id]) },
                  colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                  trailingContent = {
                    Row {
                      Text(
                          text =
                              poll.getNumberOfVotesPerOption()[id].toString() +
                                  "/" +
                                  poll.getNumberOfVotesPerOption().sum().toString(),
                          fontSize = 20.sp)
                      if (id == selectedOptionId) {
                        Icon(
                            imageVector = Icons.Rounded.RadioButtonChecked,
                            contentDescription = "option " + (id + 1) + " selected")
                      } else {
                        Icon(
                            imageVector = Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = "option " + (id + 1) + " unselected")
                      }
                    }
                  })
            }
          }
        }
      }
}
