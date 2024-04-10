package com.monkeyteam.chimpagne.ui.theme

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.viewmodel.AccountViewModel

@Composable
fun AccountCreation(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  val account = accountViewModel.userAccount.collectAsState()

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              /*TODO add uri to account*/
              Log.d("AccountCreation", "Profile picture URI: $uri")
              accountViewModel.updateUri(uri)
            } else {
              Log.d("AccountCreation", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.account_creation_screen_button,
      hasBackButton = false,
      selectedImageUri = account.value?.profilePictureUri,
      onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
      firstName = account.value?.firstName ?: "",
      firstNameLabel = R.string.account_creation_screen_first_name,
      firstNameChange = { accountViewModel.updateFirstName(it) },
      lastName = account.value?.lastName ?: "",
      lastNameLabel = R.string.account_creation_screen_last_name,
      lastNameChange = { accountViewModel.updateLastName(it) },
      location = account.value?.location?.name ?: "",
      locationLabel = R.string.account_creation_screen_city,
      locationChange = { accountViewModel.updateLocationName(it) },
      preferredLanguageEnglish =
          accountViewModel.userAccount.value?.preferredLanguageEnglish ?: true,
      onLanguageToggle = { accountViewModel.updatePreferredLanguageEnglish(it) },
      commitButtontext = R.string.account_creation_screen_button,
      commitButtonIcon = R.drawable.ic_logout,
      to_navigate_next = Route.HOME_SCREEN,
      navObject = navObject)
}
