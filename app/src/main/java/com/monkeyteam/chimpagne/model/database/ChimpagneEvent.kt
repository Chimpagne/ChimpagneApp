package com.monkeyteam.chimpagne.model.database

import com.monkeyteam.chimpagne.model.location.Location

data class ChimpagneEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location("default"),
    val isPublic: Boolean = false
)
