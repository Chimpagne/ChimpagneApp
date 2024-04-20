package com.monkeyteam.chimpagne.ui.components

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
import androidx.compose.ui.unit.dp

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
  Surface(
      modifier = Modifier.padding(end = 8.dp),
      shape = RoundedCornerShape(52.dp),
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = tag,
              modifier = Modifier.padding(start = 16.dp),
              style = MaterialTheme.typography.bodyMedium)
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
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(end = 8.dp)
  )
}
