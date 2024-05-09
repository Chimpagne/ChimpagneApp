package com.monkeyteam.chimpagne.ui.screens.supplies

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID

@Composable
fun SupplyDialogAccountEntry(
    account: ChimpagneAccount?,
    loggedUserUID: ChimpagneAccountUID,
    showCheckBox: Boolean = false,
    checked: Boolean = false,
    onCheck: (Boolean) -> Unit = {}
) {
  ListItem(
      headlineContent = {
        Text(
            text =
                "${account?.firstName} ${account?.lastName}" +
                    if (account!!.firebaseAuthUID == loggedUserUID)
                        " (${stringResource(id = R.string.you)})"
                    else "")
      },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      trailingContent = {
        if (showCheckBox) {
          Checkbox(checked = checked, onCheckedChange = onCheck)
        }
      },
      modifier = Modifier.clickable { onCheck(!checked) })
}
