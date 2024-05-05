package com.monkeyteam.chimpagne.ui.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun ChooseSocialsPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  var facebookUrl by remember { mutableStateOf(uiState.socialMediaLinks.getValue("facebook")) }
  var instagramUrl by remember { mutableStateOf(uiState.socialMediaLinks.getValue("instagram")) }
  var discordUrl by remember { mutableStateOf(uiState.socialMediaLinks.getValue("discord")) }

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.links_to_social_media),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("social_media_title"))
    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = facebookUrl,
        onUrlChange = { facebookUrl = it },
        labelResource = R.string.link_to_facebook_page,
        iconResource = R.drawable.facebook,
        testTag = "facebook_input",
        updateSocialMediaLink = { updateIfValid(eventViewModel, Pair("facebook", facebookUrl)) },
        platform = "facebook")

    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = instagramUrl,
        onUrlChange = { instagramUrl = it },
        labelResource = R.string.link_to_instagram_page,
        iconResource = R.drawable.instagram,
        testTag = "instagram_input",
        updateSocialMediaLink = { updateIfValid(eventViewModel, Pair("instagram", instagramUrl)) },
        platform = "instagram")

    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = discordUrl,
        onUrlChange = { discordUrl = it },
        labelResource = R.string.link_to_discord_page,
        iconResource = R.drawable.discord,
        testTag = "discord_input",
        updateSocialMediaLink = { updateIfValid(eventViewModel, Pair("discord", discordUrl)) },
        platform = "discord")
  }
}

@Composable
private fun SocialMediaTextField(
    url: String,
    onUrlChange: (String) -> Unit,
    labelResource: Int,
    iconResource: Int,
    testTag: String,
    updateSocialMediaLink: (Pair<String, String>) -> Unit,
    platform: String
) {
  val iconPainter: Painter = painterResource(id = iconResource)
  val errorMessage =
      remember(url) {
        if (url.isNotValid())
            "Invalid URL. Please make sure it starts with https:// or http://, or leave the field empty."
        else ""
      }

  OutlinedTextField(
      value = url,
      onValueChange = {
        onUrlChange(it)
        updateSocialMediaLink(Pair(platform, it))
      },
      label = { Text(stringResource(id = labelResource)) },
      leadingIcon = {
        Image(painter = iconPainter, contentDescription = platform, modifier = Modifier.size(35.dp))
      },
      keyboardOptions = KeyboardOptions.Default,
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      isError = url.isNotValid())

  if (url.isNotValid()) {
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(start = 16.dp, top = 2.dp))
  }
}

private fun String.isNotValid(): Boolean {
  return this.isNotEmpty() && !this.startsWith("https://") && !this.startsWith("http://")
}

private fun updateIfValid(eventViewModel: EventViewModel, pair: Pair<String, String>) {
  if (!pair.second.isNotValid()) {
    eventViewModel.updateSocialMediaLink(pair)
  }
}
