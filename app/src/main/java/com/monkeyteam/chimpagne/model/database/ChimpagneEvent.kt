package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import java.util.Calendar

// https://stackoverflow.com/questions/39815117/add-an-item-to-a-list-in-firebase-database
data class ChimpagneEvent(
    val id: String = "",
    val title: String = "Default Name",
    val description: String = "Default Description",
    val location: Location = Location(),
    val public: Boolean = false,
    val tags: List<String> = listOf(),
    val guests: Map<String, Boolean> = hashMapOf(),
    val startsAtTimestamp: Timestamp = Timestamp.now(),
    val endsAtTimestamp: Timestamp = Timestamp.now()
) {
  @get:Exclude val guestList = guests.keys

  @get:Exclude val startAt = buildCalendar(startsAtTimestamp)

  @get:Exclude val endsAt = buildCalendar(endsAtTimestamp)

  constructor(
      id: String,
      title: String,
      description: String,
      location: Location,
      public: Boolean,
      tags: List<String>,
      guests: Map<String, Boolean>,
      startAt: Calendar,
      endAt: Calendar
  ) : this(
      id,
      title,
      description,
      location,
      public,
      tags,
      guests,
      buildTimestamp(startAt),
      buildTimestamp(endAt))
}
