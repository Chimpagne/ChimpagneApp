package com.monkeyteam.chimpagne.ui.components

import com.monkeyteam.chimpagne.R

data class SocialMedia(
    val platformName: String = "",
    val platformUrl: String = "",
    val chosenGroupUrl: String = "",
    val labelResource: Int = 0,
    val iconResource: Int = 0,
    val testTag: String = ""
)

val SupportedSocialMedia =
    listOf(
        SocialMedia(
            platformName = "discord",
            platformUrl = "https://discord.gg/",
            chosenGroupUrl = "",
            labelResource = R.string.discord_group_invite_link,
            iconResource = R.drawable.discord,
            testTag = "discord_input",
        ),
        SocialMedia(
            platformName = "telegram",
            platformUrl = "https://t.me/",
            chosenGroupUrl = "",
            labelResource = R.string.telegram_group_invite_link,
            iconResource = R.drawable.telegram,
            testTag = "telegram_input",
        ),
        SocialMedia(
            platformName = "whatsapp",
            platformUrl = "https://chat.whatsapp.com/",
            chosenGroupUrl = "",
            labelResource = R.string.whatsapp_group_invite_link,
            iconResource = R.drawable.whatsapp,
            testTag = "whatsapp_input",
        ),
    )
