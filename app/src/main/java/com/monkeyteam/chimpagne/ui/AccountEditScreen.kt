package com.monkeyteam.chimpagne.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.monkeyteam.chimpagne.MainActivity
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.viewmodel.AccountViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEdit(navObject: NavigationActions, accountViewModel: AccountViewModel) {
  val account = accountViewModel.userAccount.collectAsState()

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              /*TODO add uri to account*/
              Log.d("AccountEdit", "Profile picture URI: $uri")
              accountViewModel.updateUri(uri)
            } else {
              Log.d("AccountEdit", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.accountEditScreenButton,
      hasBackButton = true,
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
      onLanguageToggle = {
        accountViewModel.updatePreferredLanguageEnglish(it)
      },
      commitButtontext = R.string.accountEditScreenButton,
      commitButtonIcon = R.drawable.edit_pen,
      commitOnClick = { accountViewModel.putUpdatedAccount() },
      to_navigate_next = Route.ACCOUNT_SETTINGS_SCREEN,
      navObject = navObject)
}

