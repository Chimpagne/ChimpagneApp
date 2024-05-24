package com.monkeyteam.chimpagne.ui.components.eventview

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@Composable
fun OrganiserView(
    ownerId: ChimpagneAccountUID,
    accountViewModel: AccountViewModel,
    event: ChimpagneEvent
) {

  val profilePictureUriState = remember { mutableStateOf<Uri?>(null) }
  val clipboardManager = LocalClipboardManager.current

  LaunchedEffect(ownerId) {
    accountViewModel.fetchAccounts(listOf(ownerId))
    accountViewModel.getProfilePictureUri(ownerId) { uri -> profilePictureUriState.value = uri }
  }

  val context = LocalContext.current

  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row {
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
          Text(
              text =
                  "${
                    stringResource(
                        id = R.string.event_details_screen_organized_by
                    )
                }\n ${accountViewModel.uiState.value.fetchedAccounts[ownerId]?.firstName ?: ""} ${accountViewModel.uiState.value.fetchedAccounts[ownerId]?.lastName ?: ""}",
              fontSize = 14.sp,
              fontFamily = ChimpagneFontFamily,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(start = 20.dp, end = 20.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier.testTag("share")
                    .shadow(6.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                      val annotatedString = buildAnnotatedString {
                        append(
                            ContextCompat.getString(context, R.string.deep_link_url_event) +
                                event.id)
                      }
                      clipboardManager.setText(annotatedString)
                    }
                    .padding(8.dp)) {
              Icon(
                  imageVector = Icons.Rounded.Share,
                  contentDescription = "Share Event",
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(36.dp))
            }
      }
}
