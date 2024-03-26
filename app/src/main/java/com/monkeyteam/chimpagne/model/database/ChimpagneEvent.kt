package com.monkeyteam.chimpagne.model.database

import com.monkeyteam.chimpagne.model.location.Location

// https://stackoverflow.com/questions/39815117/add-an-item-to-a-list-in-firebase-database
data class ChimpagneEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location("default"),
    val isPublic: Boolean = false,
    val tags: Map<String, Boolean> =  hashMapOf(),
    val guests: Map<String, Boolean> = hashMapOf()
) {
    val tagList = tags.keys
    val questList = guests.keys
}
