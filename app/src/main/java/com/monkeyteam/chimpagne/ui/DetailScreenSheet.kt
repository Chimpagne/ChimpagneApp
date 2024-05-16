package com.monkeyteam.chimpagne.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun DetailScreenSheet(
    event: ChimpagneEvent?,
    onJoinClick: () -> Unit = {},
    context: Context? = null
) {
  var showDialog by remember { mutableStateOf(false) }
  val enhancedOnJoinClick = {
    onJoinClick()
    showDialog = true
  }
  if (event != null && event.id.isNotBlank()) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = event.title,
              style = ChimpagneTypography.headlineMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.startsAt().time.toString(),
              style = ChimpagneTypography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.endsAt().time.toString(),
              style = ChimpagneTypography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.description,
              style = ChimpagneTypography.bodySmall,
              modifier = Modifier.padding(bottom = 8.dp))

          Row(
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                event.tags.forEach { tag -> SimpleTagChip(tag) }
              }

          Button(
              onClick = enhancedOnJoinClick,
              modifier = Modifier.align(Alignment.CenterHorizontally).testTag("join_button")) {
                Text(stringResource(id = R.string.find_event_join_event_button_text))
              }
        }
    if (showDialog && context != null) {
      popUpCalendar(
          onAccept = {
            createCalendarIntent(event)?.let { context.startActivity(it) }
                ?: Toast.makeText(context, R.string.calendar_failed, Toast.LENGTH_SHORT).show()
            showDialog = false
          },
          onReject = { showDialog = false },
          event = event)
    }
  } else {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
      Text(
          stringResource(id = R.string.find_event_no_event_available),
          style = ChimpagneTypography.bodyMedium)
    }
  }
}
