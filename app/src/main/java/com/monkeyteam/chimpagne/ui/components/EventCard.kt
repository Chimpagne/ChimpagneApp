package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.timestampToStringWithDateAndTime
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun EventCard(event: ChimpagneEvent, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth()) {
    Card(
        modifier =
            modifier
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .clickable { onClick() }
                .aspectRatio(1.9f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
          Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
              ImageWithBlackFilterOverlay(event.imageUri, true)
              // Adding the status overlay on top of the image
              Row(
                  horizontalArrangement = Arrangement.End,
                  modifier =
                      Modifier.align(Alignment.TopEnd)
                          .padding(top = 12.dp, end = 12.dp)
                          .background(
                              color =
                                  Color.Black.copy(
                                      alpha = 0.8f), // Semi-transparent black background
                              shape = RoundedCornerShape(100) // Rounded corners
                              )
                          .padding(
                              start = 8.dp,
                              end = 8.dp,
                              top = 4.dp,
                              bottom = 4.dp) // Padding inside the background
                  ) {
                    Text(
                        text =
                            if (event.public) stringResource(id = R.string.public_string)
                            else stringResource(id = R.string.private_string),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White, // Ensure the text is visible against the background
                        modifier = Modifier.padding(end = 8.dp))
                    Icon(
                        imageVector =
                            if (event.public) Icons.Rounded.Public else Icons.Rounded.PublicOff,
                        contentDescription = if (event.public) "Public Event" else "Private Event",
                        tint = Color.White // Ensure the icon is visible against the background
                        )
                  }
            }
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                  Text(
                      text = event.title,
                      style = ChimpagneTypography.titleLarge,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.align(Alignment.CenterHorizontally))
                  Spacer(Modifier.height(4.dp))
                  Text(
                      text = timestampToStringWithDateAndTime(event.startsAtTimestamp),
                      style = ChimpagneTypography.titleMedium,
                      modifier = Modifier.align(Alignment.CenterHorizontally))
                  Spacer(Modifier.height(4.dp))
                  Text(
                      text = event.location.name,
                      style = ChimpagneTypography.bodyMedium,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.align(Alignment.CenterHorizontally))
                }
          }
        }
  }
}
