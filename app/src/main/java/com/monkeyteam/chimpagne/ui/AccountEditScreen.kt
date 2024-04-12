package com.monkeyteam.chimpagne.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEdit(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  LaunchedEffect(Unit) { accountViewModel.moveUserAccountToTemp() }
  val tempAccount by accountViewModel.tempAccount.collectAsState()

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountEdit", "Profile picture URI: $uri")
              accountViewModel.updateUri(uri)
            } else {
              Log.d("AccountEdit", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.accountEditScreenButton,
      hasBackButton = true,
      selectedImageUri = tempAccount.profilePictureUri,
      onPickImage = { /*pickProfilePicture.launch(PickVisualMediaRequest()) TODO put back for sprint4*/},
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
      commitButtontext = R.string.accountEditScreenButton,
      commitButtonIcon = R.drawable.edit_pen,
      commitOnClick = accountViewModel::putUpdatedAccount,
      to_navigate_next = Route.ACCOUNT_SETTINGS_SCREEN,
      navObject = navObject)
}
