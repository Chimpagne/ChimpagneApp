package com.monkeyteam.chimpagne.ui.account

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.ui.components.ChimpagneSpacer
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.TopBar
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountUpdateScreen(
    accountViewModel: AccountViewModel,
    onGoBack: () -> Unit,
    onAccountUpdated: () -> Unit,
    editMode: Boolean = false
) {
  val context = LocalContext.current
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  LaunchedEffect(Unit) { accountViewModel.copyRealToTemp() }

  // I don't use accountViewModelState.loading here as it makes the screen appear twice
  var loading by remember { mutableStateOf(false) }
  if (loading) {
    SpinnerView()
    return
  }

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountChangeScreen", "Profile picture URI: $uri")
              accountViewModel.updateProfilePicture(uri)
            } else {
              Log.d("AccountChangeScreen", "Profile picture URI is null")
            }
          })

  Scaffold(
      topBar = {
        TopBar(
            text =
                stringResource(
                    id =
                        if (editMode) R.string.account_edit_screen_title
                        else R.string.account_creation_screen_title),
            navigationIcon = { GoBackButton(onClick = onGoBack) })
      }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              ChimpagneSpacer()
              ProfileIcon(
                  uri = accountViewModelState.tempProfilePicture,
                  onClick = { pickProfilePicture.launch(PickVisualMediaRequest()) },
                  size = 200.dp,
                  modifier = Modifier.testTag("profile_icon"))
              ChimpagneSpacer()
              OutlinedTextField(
                  value = accountViewModelState.tempAccount.firstName,
                  onValueChange = accountViewModel::updateFirstName,
                  label = {
                    Text(
                        stringResource(id = R.string.account_first_name),
                        Modifier.testTag("first_name_label"))
                  },
                  maxLines = 1,
                  modifier = Modifier.testTag("first_name_field"))
              ChimpagneSpacer()
              OutlinedTextField(
                  value = accountViewModelState.tempAccount.lastName,
                  onValueChange = accountViewModel::updateLastName,
                  label = {
                    Text(
                        stringResource(id = R.string.account_last_name),
                        Modifier.testTag("last_name_label"))
                  },
                  maxLines = 1,
                  modifier = Modifier.testTag("last_name_field"))
              ChimpagneSpacer()

              IconTextButton(
                  text =
                      stringResource(
                          id =
                              if (editMode) R.string.account_edit_button
                              else R.string.account_creation_button),
                  icon = Icons.Default.Save,
                  modifier = Modifier.testTag("submit_button"),
                  onClick = {
                    if (checkNotEmpty(accountViewModelState.tempAccount, context)) {
                      loading = true
                      accountViewModel.submitUpdatedAccount(
                          onSuccess = onAccountUpdated,
                          onFailure = {
                            Toast.makeText(
                                    context,
                                    getString(
                                        context,
                                        if (editMode) R.string.account_edit_failed
                                        else R.string.account_creation_failed),
                                    Toast.LENGTH_SHORT)
                                .show()
                            loading = false
                          })
                    }
                  })
            }
      }
}

/** Checks if the account is empty, and displays toasts accordingly. */
fun checkNotEmpty(account: ChimpagneAccount, context: Context): Boolean {
  return if (account.firstName == "") {
    Toast.makeText(
            context, getString(context, R.string.account_first_name_empty), Toast.LENGTH_SHORT)
        .show()
    false
  } else if (account.lastName == "") {
    Toast.makeText(
            context, getString(context, R.string.account_last_name_empty), Toast.LENGTH_SHORT)
        .show()
    false
  } else {
    true
  }
}
