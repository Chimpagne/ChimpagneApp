package com.monkeyteam.chimpagne.model.database

import com.monkeyteam.chimpagne.model.location.Location

data class ChimpagneAccount(
    val email: String = "",
    val profilePictureLink: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val location: Location = Location()
)
