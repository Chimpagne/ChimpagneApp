package com.monkeyteam.chimpagne.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
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
              modifier = Modifier.padding(bottom = 16.dp))
          Row(
              modifier = Modifier.fillMaxWidth().testTag("event date"),
              horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = stringResource(id = R.string.date_tools_from),
                      fontFamily = ChimpagneFontFamily,
                      fontSize = 16.sp,
                      color = Color.Gray)
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = simpleDateFormat(buildTimestamp(event.startsAt())),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold)
                    Text(
                        text = simpleTimeFormat(buildTimestamp(event.startsAt())),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold)
                  }
                }
                Button(
                    onClick = enhancedOnJoinClick,
                    modifier = Modifier.testTag("join_button").padding(vertical = 4.dp)) {
                      Text(stringResource(id = R.string.find_event_join_event_button_text))
                    }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                      text = stringResource(id = R.string.date_tools_until),
                      fontFamily = ChimpagneFontFamily,
                      fontSize = 16.sp,
                      color = Color.Gray)
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = simpleDateFormat(buildTimestamp(event.endsAt())),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold)
                    Text(
                        text = simpleTimeFormat(buildTimestamp(event.endsAt())),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold)
                  }
                }
              }

          var expandedDescription by remember { mutableStateOf(false) }
          val maxLines = if (expandedDescription) Int.MAX_VALUE else 3
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .testTag("description")
                      .clickable { expandedDescription = !expandedDescription }
                      .padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = event.description,
                    fontSize = 16.sp,
                    fontFamily = ChimpagneFontFamily,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = maxLines,
                    modifier = Modifier.weight(1f))
                Icon(
                    imageVector =
                        if (expandedDescription) Icons.Filled.ArrowDropUp
                        else Icons.Filled.ArrowDropDown,
                    contentDescription = if (expandedDescription) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary)
              }

          Row(modifier = Modifier.horizontalScroll(rememberScrollState()).testTag("tag list")) {
            event.tags.forEach { tag ->
              Box(
                  modifier =
                      Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                          .clip(RoundedCornerShape(50))
                          .background(MaterialTheme.colorScheme.primaryContainer)
                          .padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SimpleTagChip(tag)
                  }
            }
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
