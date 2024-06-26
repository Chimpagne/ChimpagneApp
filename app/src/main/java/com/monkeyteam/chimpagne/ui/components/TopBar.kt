package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily

/**
 * Used on every screen with a top bar. This ensure the text placement is consistent, as well as the
 * positioning and font used.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    text: String,
    actions: @Composable() (RowScope.() -> Unit) = {},
    navigationIcon: @Composable () -> Unit = {}
) {
  TopAppBar(
      title = {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = ChimpagneFontFamily,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(16.dp))
      },
      actions = actions,
      modifier =
          Modifier.shadow(4.dp, clip = false, shape = MaterialTheme.shapes.small)
              .testTag("screen title"),
      navigationIcon = navigationIcon)
}
