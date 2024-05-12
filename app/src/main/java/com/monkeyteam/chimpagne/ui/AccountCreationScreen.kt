package com.monkeyteam.chimpagne.ui.theme

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.utilities.checkNotEmpty
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@Composable
fun AccountCreation(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  val accountViewModelState by accountViewModel.uiState.collectAsState()
  val context = LocalContext.current

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountCreation", "Profile picture URI: $uri")
              accountViewModel.updateProfilePicture(uri)
            } else {
              Log.d("AccountCreation", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.account_creation_screen_button,
      hasBackButton = false,
      selectedImageUri = accountViewModelState.tempProfilePicture,
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
      commitButtontext = R.string.account_creation_screen_button,
      commitButtonIcon = R.drawable.ic_logout,
      commitOnClick = {
        if (checkNotEmpty(accountViewModelState.tempAccount, context)) {
          navObject.navigateTo(Route.LOADING)
          accountViewModel.submitUpdatedAccount(
              onSuccess = {
                navObject.clearAndNavigateTo(Route.HOME_SCREEN, true)
                Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
              },
              onFailure = {
                navObject.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
                Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show()
              })
        } else {
          Log.d("AccountCreation", "Account creation failed")
          navObject.popBackStack()
        }
      },
      navObject = navObject)
}
