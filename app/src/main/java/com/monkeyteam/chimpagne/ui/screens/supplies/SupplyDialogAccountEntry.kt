package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount

@Composable
fun SupplyDialogAccountEntry(account: ChimpagneAccount?, showCheckBox: Boolean, checked: Boolean = false, onCheck: (Boolean) -> Unit = {}) {
  ListItem(headlineContent = { Text(text = "${account?.firstName} ${account?.lastName}") },
    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    trailingContent = {
      if (showCheckBox) {
        Checkbox(checked = checked, onCheckedChange = onCheck)
      }
    })
}