package com.monkeyteam.chimpagne.ui.event.details.supplies

import androidx.compose.foundation.clickable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
        val accountName = "${account?.firstName} ${account?.lastName}"
        val accountIsYou =
            if (account!!.firebaseAuthUID == loggedUserUID)
                " (${stringResource(id = R.string.chimpagne_you)})"
            else ""
        Text(text = accountName + accountIsYou)
      },
      colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      trailingContent = {
        if (showCheckBox) {
          Checkbox(
              checked = checked,
              onCheckedChange = onCheck,
              modifier = Modifier.testTag("supply_account_checkbox"))
        }
      },
      modifier = Modifier.clickable { onCheck(!checked) }.testTag("supply_account_entry"))
}
