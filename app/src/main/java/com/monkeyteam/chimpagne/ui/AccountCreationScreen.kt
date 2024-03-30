package com.monkeyteam.chimpagne.ui.theme

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.utilities.getLanguageStrings

@Composable
fun AccountCreation(navObject: NavigationActions) {

  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var preferredLanguageEnglish by remember { mutableStateOf(false) }
  var location by remember { mutableStateOf("") }
  val languageStrings = getLanguageStrings(preferredLanguageEnglish)

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              /*TODO add uri to account*/
              Log.d("AccountCreation", "Profile picture URI: $uri")
              selectedImageUri = uri
            } else {
              Log.d("AccountCreation", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = languageStrings.createAccount,
      hasBackButton = false,
      selectedImageUri = selectedImageUri,
      onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
      firstName = firstName,
      firstNameLabel = languageStrings.firstName,
      firstNameChange = { firstName = it },
      lastName = lastName,
      lastNameLabel = languageStrings.lastName,
      lastNameChange = { lastName = it },
      location = location,
      locationLabel = languageStrings.city,
      locationChange = { location = it },
      preferredLanguageEnglish = preferredLanguageEnglish,
      onLanguageToggle = { preferredLanguageEnglish = it },
      commitButtontext = languageStrings.createAccount,
      commitButtonIcon = R.drawable.ic_logout,
      to_navigate_next = Route.HOME_SCREEN,
      navObject = navObject)
}
