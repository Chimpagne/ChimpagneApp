package com.monkeyteam.chimpagne.ui.components.eventview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun ChimpagneDivider() {
  HorizontalDivider(
      modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray)
}

@Composable
fun ChimpagneLogoDivider(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier.padding(vertical = 8.dp, horizontal = 5.dp).fillMaxWidth()) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp, style = ChimpagneTypography.titleMedium)
        HorizontalDivider(modifier = Modifier.weight(1f).padding(start = 8.dp).height(1.dp))
      }
}
