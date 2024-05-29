package com.monkeyteam.chimpagne.ui.event.polls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.TopBar
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollsAndVotingScreen(
    eventViewModel: EventViewModel,
    accountViewModel: AccountViewModel,
    onGoBack: () -> Unit
) {
  val eventUIState by eventViewModel.uiState.collectAsState()
  val accountUIState by accountViewModel.uiState.collectAsState()

  var displayCreatePollPopup by remember { mutableStateOf(false) }
  var displayVotePollPopup by remember { mutableStateOf(false) }
  var displayViewPollPopup by remember { mutableStateOf(false) }
  var selectedPollId by remember { mutableStateOf("") }

  if (displayCreatePollPopup) {
    CreatePollDialog(
        onPollCreate = {
          eventViewModel.createPollAtomically(
              poll = it, onSuccess = { displayCreatePollPopup = false })
        },
        onPollCancel = { displayCreatePollPopup = false },
        onDismissRequest = { displayCreatePollPopup = false })
  }
  if (displayVotePollPopup) {
    val poll = eventUIState.polls[selectedPollId]!!
    VotePollDialog(
        poll = poll,
        userRole = eventUIState.currentUserRole,
        onOptionVote = {
          eventViewModel.castPollVoteAtomically(
              pollId = selectedPollId, optionIndex = it, { displayViewPollPopup = true })
        },
        onPollDelete = {
          eventViewModel.deletePollAtomically(
              pollId = it, onSuccess = { displayVotePollPopup = false })
        },
        onPollCancel = { displayVotePollPopup = false },
        onDismissRequest = { displayVotePollPopup = false })
  }
  if (displayViewPollPopup) {
    displayVotePollPopup = false
    val poll = eventUIState.polls[selectedPollId]!!
    ViewPollDialog(
        poll = poll,
        selectedOptionId = poll.votes[accountUIState.currentUserUID]!!,
        userRole = eventUIState.currentUserRole,
        onPollDelete = {
          eventViewModel.deletePollAtomically(
              pollId = it, onSuccess = { displayViewPollPopup = false })
        },
        onPollCancel = { displayViewPollPopup = false },
        onDismissRequest = { displayViewPollPopup = false })
  }
  Scaffold(
      topBar = {
        TopBar(
            text = stringResource(id = R.string.polls_topbar_title),
            navigationIcon = { GoBackButton(onGoBack) })
      },
      floatingActionButton = {
        if (listOf(ChimpagneRole.OWNER, ChimpagneRole.STAFF)
            .contains(eventUIState.currentUserRole)) {
          FloatingActionButton(
              modifier = Modifier.size(70.dp), onClick = { displayCreatePollPopup = true }) {
                Icon(Icons.Rounded.Add, "create poll button")
              }
        }
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start) {
              Spacer(Modifier.height(16.dp))
              LazyColumn {
                item {
                  Legend(
                      stringResource(id = R.string.polls_legend_text),
                      imageVector = Icons.Rounded.Poll,
                      contentDescription = "poll legend text")
                }
                if (eventUIState.polls.isEmpty()) {
                  item {
                    Text(
                        text = stringResource(id = R.string.polls_empty_poll_list),
                        modifier = Modifier.padding(16.dp).testTag("empty poll list"))
                  }
                } else {
                  items(eventUIState.polls.values.toList()) { poll ->
                    ChimpagneButton(
                        modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("a poll"),
                        text = poll.title,
                        onClick = {
                          selectedPollId = poll.id
                          if (poll.votes.containsKey(accountUIState.currentUserUID)) {
                            displayViewPollPopup = true
                          } else {
                            displayVotePollPopup = true
                          }
                        })
                  }
                }
              }
            }
      }
}
