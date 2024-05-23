package com.monkeyteam.chimpagne.ui.components

import android.content.Context
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(context: Context, actions: @Composable() (RowScope.() -> Unit) = {}) {
  TopAppBar(
      title = {
        Text(
            text = context.getString(R.string.events_near_you),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp))
      },
      actions = actions,
      modifier = Modifier.shadow(4.dp, clip = false, shape = MaterialTheme.shapes.small))
}
