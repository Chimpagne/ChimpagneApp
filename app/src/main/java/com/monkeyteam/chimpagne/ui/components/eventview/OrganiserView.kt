package com.monkeyteam.chimpagne.ui.components.eventview

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@Composable
fun OrganiserView(owner: ChimpagneAccount, accountViewModel: AccountViewModel) {

  val profilePictureUriState = remember { mutableStateOf<Uri?>(null) }

  LaunchedEffect(owner.firebaseAuthUID) {
    accountViewModel.getProfilePictureUri(owner.firebaseAuthUID) { uri ->
      profilePictureUriState.value = uri
    }
  }

  val context = LocalContext.current

  Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
              .fillMaxWidth()
      ) {
          Text(
              text = stringResource(id = R.string.event_details_screen_organized_by),
              fontSize = 16.sp,
              style = ChimpagneTypography.titleLarge,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(start = 16.dp)
          )

          Spacer(modifier = Modifier.weight(2f))

          ChimpagneButton(
              text = stringResource(id = R.string.event_details_screen_report_problem),
              icon = Icons.Default.Flag,
              textStyle = ChimpagneTypography.bodyMedium,
              onClick = {
                  Toast.makeText(
                      context,
                      "This function will be implemented in a future version",
                      Toast.LENGTH_SHORT
                  ).show()
              },
              modifier = Modifier.testTag("reportProblem")
          )
      }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp)) {
          Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            profilePictureUriState.value?.let { uri ->
              ProfileIcon(
                  uri = uri,
                  onClick = {
                    Toast.makeText(
                            context,
                            "This function will be implemented in a future version",
                            Toast.LENGTH_SHORT)
                        .show()
                  })
            } ?: CircularProgressIndicator()
          }
          Text(
              text = "${owner.firstName} ${owner.lastName}",
              fontSize = 14.sp,
              fontFamily = ChimpagneFontFamily,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(start = 16.dp))
        }
  }
}
