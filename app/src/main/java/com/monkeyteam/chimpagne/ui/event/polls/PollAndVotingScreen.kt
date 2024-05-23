package com.monkeyteam.chimpagne.ui.event.polls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollsAndVotingScreen(
    eventViewModel: EventViewModel,
    onGoBack: () -> Unit
) {
    val eventUIState by eventViewModel.uiState.collectAsState()
    var displayCreatePollPopup by remember { mutableStateOf(false) }
    var displayViewPollPopup by remember { mutableStateOf(false) }

    if(displayCreatePollPopup){
        CreatePollDialog(
            onPollCreate = {eventViewModel.createPollAtomically(poll = it)},
            onPollCancel = {displayCreatePollPopup = false},
            onDismissRequest = {displayCreatePollPopup = false}
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Polls And Voting",
                        Modifier.testTag("screen title"))
                },
                modifier = Modifier.shadow(4.dp),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onGoBack()
                        }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                    }
                })
        },
        floatingActionButton = {
            if (listOf(ChimpagneRole.OWNER, ChimpagneRole.STAFF)
                    .contains(eventUIState.currentUserRole)) {
                FloatingActionButton(modifier = Modifier.size(70.dp), onClick = { displayCreatePollPopup = true }) {
                    Icon(Icons.Rounded.Add, "create poll button")
                }
            }
        }) { innerPadding ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start) {
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                item {
                    Legend(
                        text = "Event Polls",
                        imageVector = Icons.Rounded.Poll,
                        contentDescription = "Poll List")
                }
                if (eventUIState.polls.isEmpty()) {
                    item {
                        Text(
                            text = "There were no polls created for this event",
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("empty poll list"))
                    }
                } else {
                    items(eventUIState.polls.values.toList()) { poll ->
                        ChimpagneButton(
                            modifier = Modifier.testTag("a poll"),
                            text = poll.title,
                            onClick = {
                                //TODO To be implemented in another PR//
                            }
                        )
                    }
                }
            }
        }
    }
}