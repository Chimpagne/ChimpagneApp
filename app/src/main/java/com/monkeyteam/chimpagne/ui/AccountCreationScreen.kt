package com.monkeyteam.chimpagne.ui.theme

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.viewmodel.AccountViewModel

@Composable
fun AccountCreation(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  val account by accountViewModel.account.collectAsState()
  val tempAccount by accountViewModel.tempAccount.collectAsState()

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountCreation", "Profile picture URI: $uri")
              accountViewModel.updateUri(uri)
            } else {
              Log.d("AccountCreation", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.account_creation_screen_button,
      hasBackButton = false,
      selectedImageUri = tempAccount.profilePictureUri,
      onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
      firstName = tempAccount.firstName,
      firstNameLabel = R.string.account_creation_screen_first_name,
      firstNameChange = accountViewModel::updateFirstName,
      lastName = tempAccount.lastName,
      lastNameLabel = R.string.account_creation_screen_last_name,
      lastNameChange = accountViewModel::updateLastName,
      location = tempAccount.location,
      locationLabel = R.string.account_creation_screen_city,
      locationChange = accountViewModel::updateLocation,
      preferredLanguageEnglish = tempAccount.preferredLanguageEnglish,
      onLanguageToggle = accountViewModel::updatePreferredLanguageEnglish,
      commitButtontext = R.string.account_creation_screen_button,
      commitButtonIcon = R.drawable.ic_logout,
      to_navigate_next = Route.HOME_SCREEN,
      commitOnClick = { accountViewModel.createAccount() },
      navObject = navObject)
}
