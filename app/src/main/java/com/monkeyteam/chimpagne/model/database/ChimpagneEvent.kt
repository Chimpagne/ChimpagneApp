package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.monkeyteam.chimpagne.model.location.Location
import java.util.Calendar

// https://stackoverflow.com/questions/39815117/add-an-item-to-a-list-in-firebase-database
data class ChimpagneEvent(
    val id: String = "",
    val title: String = "Default Name",
    val description: String = "Default Description",
    val location: Location = Location("default"),
    val isPublic: Boolean = false,
    val tags: List<String> =  listOf(),
    val guests: Map<String, Boolean> = hashMapOf(),
    val startsAtTimestamp: Timestamp = Timestamp.now(),
    val endsAtTimestamp: Timestamp = Timestamp.now()
) {
    @get:Exclude
    val guestList = guests.keys

    @get:Exclude
    val startAt = buildCalendarFromTimestamp(startsAtTimestamp)

    @get:Exclude
    val endsAt = buildCalendarFromTimestamp(endsAtTimestamp)
}

private fun buildCalendarFromTimestamp(timestamp: Timestamp): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = timestamp.toDate()
    return calendar
}
