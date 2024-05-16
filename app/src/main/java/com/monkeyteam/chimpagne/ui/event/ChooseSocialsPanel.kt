package com.monkeyteam.chimpagne.ui.event

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
import com.monkeyteam.chimpagne.ui.components.SocialMedia
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun ChooseSocialsPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  val socialMediaStates =
      remember(uiState.socialMediaLinks) {
        uiState.socialMediaLinks.mapValues { mutableStateOf(it.value.platformName) }
      }

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.links_to_social_media),
        style = ChimpagneTypography.headlineSmall,
        modifier = Modifier.testTag("social_media_title"))
    Spacer(modifier = Modifier.height(16.dp))

    for ((platform) in socialMediaStates) {
      SocialMediaTextField(
          socialMedia = uiState.socialMediaLinks[platform]!!,
          updateSocialMediaLink = { eventViewModel.updateSocialMediaLink(it) })
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun SocialMediaTextField(
    socialMedia: SocialMedia,
    updateSocialMediaLink: (SocialMedia) -> Unit,
) {
  val iconPainter: Painter = painterResource(id = socialMedia.iconResource)
  var hasError by remember { mutableStateOf(false) }
  var currentChosenUrl by remember { mutableStateOf(socialMedia.chosenGroupUrl) }

  OutlinedTextField(
      value = currentChosenUrl,
      onValueChange = { newUrl ->
        currentChosenUrl = newUrl
        hasError = (!newUrl.startsWith(socialMedia.platformUrl) and newUrl.isNotEmpty())
        updateSocialMediaLink(
            socialMedia.copy(
                chosenGroupUrl =
                    if (!hasError) createFullUrl(socialMedia.platformUrl, newUrl) else ""))
      },
      label = { Text(stringResource(id = socialMedia.labelResource)) },
      leadingIcon = {
        Image(
            painter = iconPainter,
            contentDescription = socialMedia.platformName,
            modifier = Modifier.size(35.dp))
      },
      keyboardOptions = KeyboardOptions.Default,
      modifier = Modifier.fillMaxWidth().testTag(socialMedia.testTag))

  if (hasError) {
    Text(
        "Invalid URL. Must start with: ${socialMedia.platformUrl} or be empty",
        color = MaterialTheme.colorScheme.error,
        style = ChimpagneTypography.bodySmall,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp))
  }
}

private fun createFullUrl(platformUrl: String, url: String): String {
  return if (url.isEmpty()) "" else platformUrl + url.removePrefix(platformUrl)
}
