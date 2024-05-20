package com.monkeyteam.chimpagne.ui.components.eventview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun EventDescription(description: String, collapsable: Boolean) {
  var expandedDescription by remember { mutableStateOf(!collapsable) }
  val maxLines = if (expandedDescription) Int.MAX_VALUE else 3
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("description")
              .then(
                  if (collapsable) Modifier.clickable { expandedDescription = !expandedDescription }
                  else Modifier)
              .padding(horizontal = 16.dp)) {
        Text(
            text = description.ifEmpty { stringResource(R.string.event_description_is_empty) },
            style = ChimpagneTypography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = maxLines,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f))
        if (collapsable) {
          Icon(
              imageVector =
                  if (expandedDescription) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
              contentDescription = if (expandedDescription) "Collapse" else "Expand",
              tint = MaterialTheme.colorScheme.primary)
        }
      }
}
