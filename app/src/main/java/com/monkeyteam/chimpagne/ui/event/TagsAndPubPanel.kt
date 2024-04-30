package com.monkeyteam.chimpagne.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Checkbox
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
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.TagField
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun TagsAndPubPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  var tagFieldActive by remember { mutableStateOf(true) }

  Column(modifier = Modifier.padding(16.dp)) {
    Legend(
        stringResource(id = R.string.event_creation_screen_tags_legend), Icons.Rounded.Tag, "Tags")

    TagField(
        uiState.tags,
        eventViewModel::updateEventTags,
        { tagFieldActive = it },
        Modifier.fillMaxWidth().testTag("tag_field"))

    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_public_legend),
        Icons.Rounded.Public,
        "Public")

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
          checked = uiState.public,
          onCheckedChange = { eventViewModel.updateEventPublicity(!uiState.public) })
      if (uiState.public)
          Text(stringResource(id = R.string.event_creation_screen_event_made_public))
      else Text(stringResource(id = R.string.event_creation_screen_make_event_public))
    }
  }
}
