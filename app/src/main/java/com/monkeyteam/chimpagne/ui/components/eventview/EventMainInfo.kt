package com.monkeyteam.chimpagne.ui.components.eventview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily

@Composable
fun EventMainInfo(event: ChimpagneEvent) {

  val context = LocalContext.current

  Column(
      modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
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
              CalendarButton(event = event, contextMainActivity = context)
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
              Box(
                  modifier =
                      Modifier.testTag("number_of_guests")
                          .clip(RoundedCornerShape(50))
                          .background(MaterialTheme.colorScheme.primaryContainer)
                          .padding(horizontal = 24.dp, vertical = 12.dp)) {
                    Text(
                        text =
                            buildAnnotatedString {
                              withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${event.guests.count()} ")
                              }
                              append(
                                  stringResource(
                                      id = R.string.event_details_screen_number_of_guests))
                            },
                        fontFamily = ChimpagneFontFamily,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                  }
            }
      }
}
