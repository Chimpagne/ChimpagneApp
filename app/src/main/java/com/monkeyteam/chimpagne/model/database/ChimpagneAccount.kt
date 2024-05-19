package com.monkeyteam.chimpagne.model.database

import android.net.Uri
import com.monkeyteam.chimpagne.model.location.Location

typealias ChimpagneAccountUID = String

data class ChimpagneAccount(
    val firebaseAuthUID: ChimpagneAccountUID = "",
    val firstName: String = "",
    val lastName: String = "",
    val location: Location = Location(),
    val imageUri: Uri = Uri.EMPTY,
    val joinedEvents: Map<ChimpagneEventId, Boolean> = hashMapOf()
)
