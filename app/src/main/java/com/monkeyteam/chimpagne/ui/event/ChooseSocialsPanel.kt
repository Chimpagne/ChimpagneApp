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

  var instagramUrl by remember { mutableStateOf(uiState.socialMediaLinks.getValue("instagram")) }
  var discordUrl by remember { mutableStateOf(uiState.socialMediaLinks.getValue("discord")) }

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.links_to_social_media),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("social_media_title"))
    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = instagramUrl,
        onUrlChange = { instagramUrl = it },
        labelResource = R.string.instagram_username,
        iconResource = R.drawable.instagram,
        testTag = "instagram_input",
        updateSocialMediaLink = { eventViewModel.updateSocialMediaLink(it) },
        platform = "https://instagram.com/")

    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = discordUrl,
        onUrlChange = { discordUrl = it },
        labelResource = R.string.discord_invite_code,
        iconResource = R.drawable.discord,
        testTag = "discord_input",
        updateSocialMediaLink = { eventViewModel.updateSocialMediaLink(it) },
        platform = "https://discord.gg/")
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

  OutlinedTextField(
      value = url,
      onValueChange = {
        val fullUrl = createFullUrl(platform, it)
        onUrlChange(fullUrl)
        updateSocialMediaLink(Pair(platform, fullUrl))
      },
      label = { Text(stringResource(id = labelResource)) },
      leadingIcon = {
        Image(painter = iconPainter, contentDescription = platform, modifier = Modifier.size(35.dp))
      },
      keyboardOptions = KeyboardOptions.Default,
      modifier = Modifier.fillMaxWidth().testTag(testTag))
}

private fun createFullUrl(platform: String, url: String): String {
  return if (url.isEmpty()) "" else "$platform$url"
}
