package com.monkeyteam.chimpagne.ui.components

import com.monkeyteam.chimpagne.R

data class SocialMedia(
    val platformName: String = "",
    val platformUrls: List<String> = emptyList(),
    val chosenGroupUrl: String = "",
    val labelResource: Int = 0,
    val iconResource: Int = 0,
    val testTag: String = ""
)

val SupportedSocialMedia =
    listOf(
        SocialMedia(
            platformName = "discord",
            platformUrls =
                List(2) {
                  "https://discord.gg/"
                  "https://discord.com/invite/"
                },
            chosenGroupUrl = "",
            labelResource = R.string.discord_group_invite_link,
            iconResource = R.drawable.discord,
            testTag = "discord_input",
        ),
        SocialMedia(
            platformName = "telegram",
            platformUrls = List(1) { "https://t.me/" },
            chosenGroupUrl = "",
            labelResource = R.string.telegram_group_invite_link,
            iconResource = R.drawable.telegram,
            testTag = "telegram_input",
        ),
        SocialMedia(
            platformName = "whatsapp",
            platformUrls = List(1) { "https://chat.whatsapp.com/" },
            chosenGroupUrl = "",
            labelResource = R.string.whatsapp_group_invite_link,
            iconResource = R.drawable.whatsapp,
            testTag = "whatsapp_input",
        ),
    )

fun convertSMLinksToSM(socialMediaLinks: Map<String, String>): Map<String, SocialMedia> {
  val socialMediaMap = SupportedSocialMedia.associateBy { it.platformName }
  return socialMediaLinks
      .mapNotNull { (key, url) ->
        socialMediaMap[key]?.let { key to it.copy(chosenGroupUrl = url) }
      }
      .toMap()
}

fun convertSMToSMLinks(socialMedia: Map<String, SocialMedia>): Map<String, String> {
  return socialMedia.mapValues { it.value.chosenGroupUrl }
}
