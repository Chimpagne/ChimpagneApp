package com.monkeyteam.chimpagne.ui.components.eventview

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun OrganiserView(
    owner : ChimpagneAccount
) {

    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        ProfileIcon(
            uri = owner.imageUri,
            onClick = {
                Toast.makeText(
                    context,
                    "This function will be implemented in a future version",
                    Toast.LENGTH_SHORT)
                    .show()
            })
        Text(
            text =
            "${stringResource(id = R.string.event_details_screen_organized_by)}\n ${owner.firstName} ${owner.lastName}",
            fontSize = 14.sp,
            fontFamily = ChimpagneFontFamily,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        ChimpagneButton(
            text = stringResource(id = R.string.event_details_screen_report_problem),
            icon = Icons.Default.Warning,
            textStyle = ChimpagneTypography.displaySmall,
            onClick = {
                Toast.makeText(
                    context,
                    "This function will be implemented in a future version",
                    Toast.LENGTH_SHORT)
                    .show()
            },
            modifier = Modifier.testTag("reportProblem"))
    }
}