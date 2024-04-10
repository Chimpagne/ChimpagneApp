package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Legend(text: String, imageVector: ImageVector, contentDescription: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(imageVector = imageVector, contentDescription = contentDescription)
    Text(
      text = text,
      modifier = Modifier.padding(8.dp),
      style = MaterialTheme.typography.titleLarge,
    )
  }
}