package com.monkeyteam.chimpagne.model.database

import com.monkeyteam.chimpagne.model.location.Location

data class ChimpagneAccount(
    val firebaseAuthUID: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val location: Location = Location()
)
