package com.monkeyteam.chimpagne.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions

@Composable
fun AccountCreation(navObject: NavigationActions) {
  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var preferredLanguageEnglish by remember { mutableStateOf(false) }
  var location by remember { mutableStateOf("") }
  val languageStrings = getLanguageStrings(preferredLanguageEnglish)

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
      // verticalArrangement = Arrangement.Center
      ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = languageStrings.createAccount,
            fontSize = 24.sp,
            modifier = Modifier.testTag("accountCreationLabel"))
        Spacer(modifier = Modifier.padding(16.dp))
        IconButton(
            onClick = { /* TODO showPhotoChooser*/},
            modifier = Modifier.size(100.dp).border(1.dp, Color.Black, CircleShape)) {
              Icon(
                  painter = painterResource(id = R.drawable.ic_placeholder_profile),
                  contentDescription = "Placeholder for user icon")
            }
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            modifier = Modifier.testTag("firstNameTextField"),
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text(languageStrings.firstName) })
        Spacer(modifier = Modifier.padding(16.dp))
        OutlinedTextField(
            modifier = Modifier.testTag("lastNameTextField"),
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text(languageStrings.lastName) })
        Spacer(modifier = Modifier.padding(16.dp))

        OutlinedTextField(
            modifier = Modifier.testTag("locationTextField"),
            value = location,
            onValueChange = { location = it },
            label = { Text(languageStrings.city) })
        Spacer(modifier = Modifier.padding(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Language")
          Spacer(modifier = Modifier.padding(16.dp))
          Switch(
              modifier = Modifier.testTag("changeLanguageSwitch"),
              checked = preferredLanguageEnglish,
              onCheckedChange = { preferredLanguageEnglish = it },
              thumbContent = {
                if (preferredLanguageEnglish) {
                  Icon(
                      painter = painterResource(id = R.drawable.ic_english),
                      contentDescription = "English")
                } else {
                  Icon(
                      painter = painterResource(id = R.drawable.ic_french),
                      contentDescription = "French")
                }
              })
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = { navObject.navigateTo("Home") /*TODO: createAccount()*/ },
            modifier = Modifier.width(210.dp).height(50.dp).testTag("createAccountButton")) {
              Icon(
                  painter = painterResource(id = R.drawable.ic_logout),
                  contentDescription = "Logout icon")
              Spacer(modifier = Modifier.padding(8.dp))
              Text(
                  languageStrings.createAccountButton,
                  modifier = Modifier.testTag("createAccountButton"))
            }
      }
}

data class LanguageStrings(
    val createAccount: String,
    val firstName: String,
    val lastName: String,
    val city: String,
    val language: String,
    val createAccountButton: String
)

@Composable
fun getLanguageStrings(preferredLanguageEnglish: Boolean): LanguageStrings {
  return if (preferredLanguageEnglish) {
    LanguageStrings(
        createAccount = "Create your Account",
        firstName = "First Name",
        lastName = "Last Name",
        city = "Choose your City",
        language = "Language",
        createAccountButton = "Create Account")
  } else {
    LanguageStrings(
        createAccount = "Créer votre compte",
        firstName = "Prénom",
        lastName = "Nom de famille",
        city = "Choisissez votre ville",
        language = "Langue",
        createAccountButton = "Créer un compte")
  }
}
