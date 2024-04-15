package com.monkeyteam.chimpagne.ui.utilities

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.md_theme_light_primary

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    label: Int,
    value: String,
    onValueChange: (String) -> Unit
) {
  OutlinedTextField(
      modifier = modifier,
      value = value,
      onValueChange = onValueChange,
      label = { Text(stringResource(id = label)) })
  Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun LanguageSwitch(
    modifier: Modifier = Modifier,
    isEnglish: Boolean,
    onToggleLanguage: (Boolean) -> Unit
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text("Language")
    Spacer(modifier = Modifier.width(16.dp))
    Switch(
        modifier = modifier,
        checked = isEnglish,
        onCheckedChange = onToggleLanguage,
        thumbContent = {
          if (isEnglish) {
            Icon(
                painter = painterResource(id = R.drawable.ic_english),
                contentDescription = "English")
          } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_french), contentDescription = "French")
          }
        })
  }
}

@Composable
fun SaveChangesButton(
    onClick: () -> Unit,
    text: Int,
    iconId: Int = R.drawable.ic_logout,
    contentDescription: String? = "Save icon",
) {
  Button(onClick = onClick, modifier = Modifier.width(210.dp).height(50.dp)) {
    Icon(painter = painterResource(id = iconId), contentDescription = contentDescription)
    Spacer(modifier = Modifier.width(8.dp))
    Text(stringResource(id = text), modifier = Modifier.testTag("saveChangesButton"))
  }
}

@Composable
fun ProfileImage(imageUri: Uri?, onClick: () -> Unit = {}) {
  IconButton(
      onClick = { onClick() },
      modifier = Modifier.size(100.dp).border(1.dp, Color.Black, CircleShape)) {
        AsyncImage(
            model = imageUri ?: R.drawable.ic_placeholder_profile,
            contentDescription = "Profile Picture",
        )
      }
  Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AccountChangeBody(
    topBarText: Int,
    hasBackButton: Boolean,
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    firstName: String,
    firstNameLabel: Int,
    firstNameChange: (String) -> Unit,
    lastName: String,
    lastNameLabel: Int,
    lastNameChange: (String) -> Unit,
    location: Location,
    locationLabel: Int,
    locationChange: (Location) -> Unit,
    commitButtontext: Int,
    commitButtonIcon: Int,
    commitOnClick: () -> Unit = {},
    navObject: NavigationActions,
) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = stringResource(id = topBarText),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("accountCreationLabel"))
                  }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = md_theme_light_primary, // Purple
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White),
            navigationIcon = {
              if (hasBackButton) {
                IconButton(onClick = { navObject.goBack() }) {
                  Icon(
                      painter = painterResource(id = R.drawable.go_back),
                      contentDescription = "Back")
                }
              }
            })
      }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(10.dp))
              ProfileImage(imageUri = selectedImageUri, onClick = onPickImage)
              TextInputField(
                  modifier = Modifier.fillMaxWidth().testTag("firstNameTextField"),
                  label = firstNameLabel,
                  value = firstName,
                  onValueChange = firstNameChange)
              TextInputField(
                  modifier = Modifier.fillMaxWidth().testTag("lastNameTextField"),
                  label = lastNameLabel,
                  value = lastName,
                  onValueChange = lastNameChange)
              LocationSelector(selectedLocation = location, updateSelectedLocation = locationChange)
              SaveChangesButton(
                  onClick = commitOnClick,
                  text = commitButtontext,
                  iconId = commitButtonIcon,
                  contentDescription = "Logout icon")
            }
      }
}
