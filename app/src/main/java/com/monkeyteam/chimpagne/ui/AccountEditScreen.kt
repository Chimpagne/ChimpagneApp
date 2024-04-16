package com.monkeyteam.chimpagne.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.utilities.checkNotEmpty
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEdit(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  LaunchedEffect(Unit) { accountViewModel.copyRealToTemp() }
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  val context = LocalContext.current

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountEdit", "Profile picture URI: $uri")
              accountViewModel.updatePicture(uri)
            } else {
              Log.d("AccountEdit", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.accountEditScreenButton,
      hasBackButton = true,
      selectedImageUri = accountViewModelState.tempImageUri,
      onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
      firstName = accountViewModelState.tempAccount.firstName,
      firstNameLabel = R.string.account_creation_screen_first_name,
      firstNameChange = accountViewModel::updateFirstName,
      lastName = accountViewModelState.tempAccount.lastName,
      lastNameLabel = R.string.account_creation_screen_last_name,
      lastNameChange = accountViewModel::updateLastName,
      location = accountViewModelState.tempAccount.location,
      locationLabel = R.string.account_creation_screen_city,
      locationChange = accountViewModel::updateLocation,
      commitButtontext = R.string.accountEditScreenButton,
      commitButtonIcon = R.drawable.edit_pen,
      commitOnClick = {
        if (checkNotEmpty(accountViewModelState.tempAccount, context)) {
          navObject.navigateTo(Route.LOADING)
          accountViewModel.submitUpdatedAccount(
              onSuccess = {
                // Delete last location of navobject
                navObject.popBackStack()
                navObject.navigateTo(Route.ACCOUNT_SETTINGS_SCREEN)
                Toast.makeText(context, "Account updated", Toast.LENGTH_SHORT).show()
              },
              onFailure = {
                navObject.popBackStack()
                navObject.navigateTo(Route.HOME_SCREEN)
                Toast.makeText(context, "Failed to update account", Toast.LENGTH_SHORT).show()
              })
        } else {
          Log.d("AccountEdit", "Account update failed")
        }
      },
      navObject = navObject)
}
