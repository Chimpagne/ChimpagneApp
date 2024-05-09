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
  var whatsappURL by remember { mutableStateOf(uiState.socialMediaLinks.getValue("whatsapp")) }

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.links_to_social_media),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("social_media_title"))
    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = instagramUrl,
        onUrlChange = { instagramUrl = it },
        labelResource = R.string.instagram_group_invite_link,
        iconResource = R.drawable.instagram,
        testTag = "instagram_input",
        updateSocialMediaLink = { eventViewModel.updateSocialMediaLink(it) },
        platformName = "instagram",
        platformUrl = "https://instagram.com/")

    Spacer(modifier = Modifier.height(16.dp))

    SocialMediaTextField(
        url = whatsappURL,
        onUrlChange = { whatsappURL = it },
        labelResource = R.string.whatsapp_group_invite_link,
        iconResource = R.drawable.whatsapp,
        testTag = "whatsapp_input",
        updateSocialMediaLink = { eventViewModel.updateSocialMediaLink(it) },
        platformName = "whatsapp",
        platformUrl = "https://chat.whatsapp.com/")
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
    platformName: String,
    platformUrl: String
) {
  val iconPainter: Painter = painterResource(id = iconResource)
  var hasError by remember { mutableStateOf(false) }

  OutlinedTextField(
      value = url,
      onValueChange = { newUrl ->
        onUrlChange(newUrl)
        hasError = !newUrl.startsWith(platformUrl)
        if (!hasError) {
          updateSocialMediaLink(Pair(platformName, newUrl))
        }
      },
      label = { Text(stringResource(id = labelResource)) },
      leadingIcon = {
        Image(
            painter = iconPainter,
            contentDescription = platformName,
            modifier = Modifier.size(35.dp))
      },
      keyboardOptions = KeyboardOptions.Default,
      modifier = Modifier.fillMaxWidth().testTag(testTag))

  if (hasError) {
    Text(
        "Invalid URL. Must start with: $platformUrl",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp))
  }
}

private fun createFullUrl(platform: String, url: String): String {
  return if (url.isEmpty()) "" else "$platform$url"
}
