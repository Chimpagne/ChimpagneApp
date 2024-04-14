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

  val account by accountViewModel.userChimpagneAccount.collectAsState()
  val tempAccount by accountViewModel.tempChimpagneAccount.collectAsState()
  val context = LocalContext.current

  val pickProfilePicture =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = { uri: Uri? ->
            if (uri != null) {
              Log.d("AccountCreation", "Profile picture URI: $uri")
              accountViewModel.updatePicture(uri)
            } else {
              Log.d("AccountCreation", "Profile picture URI is null")
            }
          })

  AccountChangeBody(
      topBarText = R.string.account_creation_screen_button,
      hasBackButton = false,
      selectedImageUri = accountViewModel.tempImageUri.collectAsState().value,
      onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
      firstName = tempAccount.firstName,
      firstNameLabel = R.string.account_creation_screen_first_name,
      firstNameChange = accountViewModel::updateFirstName,
      lastName = tempAccount.lastName,
      lastNameLabel = R.string.account_creation_screen_last_name,
      lastNameChange = accountViewModel::updateLastName,
      location = tempAccount.location,
      locationChange = accountViewModel::updateLocation,
      commitButtontext = R.string.account_creation_screen_button,
      commitButtonIcon = R.drawable.ic_logout,
      commitOnClick = {
          if (checkNotEmpty(tempAccount, context)){
              navObject.navigateTo(Route.LOADING)
              accountViewModel.createAccount(
                  onSuccess = {
                      navObject.navigateTo(Route.HOME_SCREEN)
                      Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                  },
                  onFailure = {
                      navObject.navigateTo(Route.LOGIN_SCREEN)
                      Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show()
                  })
          }else{
                Log.d("AccountCreation", "Account creation failed")
              navObject.popBackStack()
          }
      },
      navObject = navObject)
}
