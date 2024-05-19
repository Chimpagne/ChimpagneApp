package com.monkeyteam.chimpagne.ui.components.eventview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily

@Composable
fun EventMainInfo(
    event: ChimpagneEvent
) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .padding(horizontal = 40.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().testTag("event date"),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.date_tools_from),
                    fontFamily = ChimpagneFontFamily,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = simpleDateFormat(event.startsAtTimestamp),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = simpleTimeFormat(event.startsAtTimestamp),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            CalendarButton(
                event = event,
                contextMainActivity = context
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.date_tools_until),
                    fontFamily = ChimpagneFontFamily,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = simpleDateFormat(event.endsAtTimestamp),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = simpleTimeFormat(event.endsAtTimestamp),
                        fontFamily = ChimpagneFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "${event.guests.count()} ${stringResource(id = R.string.event_details_screen_number_of_guests)}",
                    fontFamily = ChimpagneFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.testTag("number of guests")
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val annotatedString = buildAnnotatedString {
                        append(ContextCompat.getString(context, R.string.deep_link_url_event) + event.id)
                    }
                    clipboardManager.setText(annotatedString)
                },
                modifier = Modifier.size(36.dp).testTag("share")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Share Event",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}