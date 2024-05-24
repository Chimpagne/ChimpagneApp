package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
  Surface(
      modifier = Modifier.padding(end = 8.dp),
      shape = RoundedCornerShape(100),
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = tag,
              modifier = Modifier.padding(start = 16.dp),
              style = ChimpagneTypography.bodyMedium)
          IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Remove tag")
          }
        }
      }
}

@Composable
fun SimpleTagChip(tag: String) {
  Text(
      text = "#$tag",
      style = ChimpagneTypography.bodyLarge,
      color = MaterialTheme.colorScheme.onPrimaryContainer,
      modifier = Modifier.padding(end = 8.dp))
}

@Composable
fun EventTagChip(tag: String) {
  Box(
      modifier =
          Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              .clip(RoundedCornerShape(50))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .padding(horizontal = 16.dp, vertical = 8.dp)) {
        SimpleTagChip(tag)
      }
}
