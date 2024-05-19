package com.monkeyteam.chimpagne.ui.components.eventview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChimpagneDivider() {
  HorizontalDivider(
      modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray)
}
