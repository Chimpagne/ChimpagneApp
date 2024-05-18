package com.monkeyteam.chimpagne.ui.account

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.ChimpagneSpacer
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountChangeScreen(
  navObject: NavigationActions,
  accountViewModel: AccountViewModel,
  onSuccess: () -> Unit,
  onFailure: () -> Unit,
  editMode: Boolean = false
) {
  val context = LocalContext.current
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  LaunchedEffect(Unit) { accountViewModel.copyRealToTemp() }

  if (accountViewModelState.loading) {
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
      TopAppBar(
        title = { Text(stringResource(id = if (editMode) R.string.accountEditScreenButton else R.string.account_creation_screen_button)) },
        modifier = Modifier.shadow(4.dp),
        navigationIcon = { GoBackButton(navObject) })
    }
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxWidth().padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      ChimpagneSpacer()
      ProfileIcon(uri = null) {
        pickProfilePicture.launch(PickVisualMediaRequest())
      }
      ChimpagneSpacer()
      OutlinedTextField(value = accountViewModelState.tempAccount.firstName, onValueChange = accountViewModel::updateFirstName, label = { Text(stringResource(id = R.string.account_creation_screen_first_name)) }, maxLines = 1)
      ChimpagneSpacer()
      OutlinedTextField(value = accountViewModelState.tempAccount.lastName, onValueChange = accountViewModel::updateLastName, label = { Text(stringResource(id = R.string.account_creation_screen_last_name)) }, maxLines = 1)
      ChimpagneSpacer()
      Button(onClick = {
        if (checkNotEmpty(accountViewModelState.tempAccount, context)) {
          accountViewModel.submitUpdatedAccount(
            onSuccess = {
              Toast.makeText(
                context,
                getString(context, if (editMode) R.string.account_edited_toast else R.string.account_created_toast),
                Toast.LENGTH_SHORT
              )
                .show()
              onSuccess()
            },
            onFailure = {
              Toast.makeText(
                context,
                getString(context, if (editMode) R.string_account_edit_failed else R.string.acount_creation_failed_toast),
                Toast.LENGTH_SHORT
              )
                .show()
              onFailure()
            })
        }
      }) {
        Text(text = "SUBMIT")
      }
    }
  }
}
