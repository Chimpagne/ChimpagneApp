package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChimpagneSpacer(width: Dp = 16.dp, height: Dp = 16.dp) {
  Spacer(Modifier.width(width).height(height))
}
