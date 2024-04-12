package com.monkeyteam.chimpagne.model.database

import android.net.Uri
import com.monkeyteam.chimpagne.model.location.Location

data class ChimpagneAccount(
    val email: String = "",
    val profilePictureUri: Uri? = null,
    val firstName: String = "",
    val lastName: String = "",
    val preferredLanguageEnglish: Boolean = true,
    val location: Location = Location()
)
