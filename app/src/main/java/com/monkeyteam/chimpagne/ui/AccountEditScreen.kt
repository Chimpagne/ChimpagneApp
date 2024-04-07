package com.monkeyteam.chimpagne.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.user.Account
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.AccountChangeBody
import com.monkeyteam.chimpagne.ui.utilities.getLanguageStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEdit(navObject: NavigationActions) {
    // Delete After
    val account =
        Account(
            email = "",
            profilePictureUri = null, // Placeholder for example URI
            firstName = "John",
            lastName = "Doe",
            preferredLanguageEnglish = true,
            location = Location("New York", 40.7128, -74.0060)
        )

    var selectedImageUri by remember { mutableStateOf<Uri?>(account.profilePictureUri) }
    var firstName by remember { mutableStateOf(account.firstName) }
    var lastName by remember { mutableStateOf(account.lastName) }
    var preferredLanguageEnglish by remember { mutableStateOf(account.preferredLanguageEnglish) }
    var location by remember { mutableStateOf(account.location?.name) }
    val languageStrings = getLanguageStrings(preferredLanguageEnglish)

    val pickProfilePicture =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri: Uri? ->
                if (uri != null) {
                    /*TODO add uri to account*/
                    Log.d("AccountEdit", "Profile picture URI: $uri")
                    selectedImageUri = uri
                } else {
                    Log.d("AccountEdit", "Profile picture URI is null")
                }
            })

    AccountChangeBody(
        topBarText = languageStrings.editAccount,
        hasBackButton = true,
        selectedImageUri = selectedImageUri,
        onPickImage = { pickProfilePicture.launch(PickVisualMediaRequest()) },
        firstName = firstName,
        firstNameLabel = languageStrings.firstName,
        firstNameChange = { firstName = it },
        lastName = lastName,
        lastNameLabel = languageStrings.lastName,
        lastNameChange = { lastName = it },
        location = location ?: "",
        locationLabel = languageStrings.city,
        locationChange = { location = it },
        preferredLanguageEnglish = preferredLanguageEnglish,
        onLanguageToggle = { preferredLanguageEnglish = it },
        commitButtontext = languageStrings.saveAccountButton,
        commitButtonIcon = R.drawable.edit_pen,
        to_navigate_next = Route.ACCOUNT_SETTINGS_SCREEN,
        navObject = navObject
    )
}
