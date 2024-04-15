package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
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
    val groceries: List<String>,
    val guests: Map<String, Boolean> = hashMapOf(),
    val startsAtTimestamp: Timestamp = Timestamp.now(),
    val endsAtTimestamp: Timestamp = Timestamp.now()
) {

  fun guestList(): Set<String> {
    return guests.keys
  }

  fun startsAt(): Calendar {
    return buildCalendar(startsAtTimestamp)
  }

  fun endsAt(): Calendar {
    return buildCalendar(endsAtTimestamp)
  }

  constructor(
      id: String,
      title: String,
      description: String,
      location: Location,
      public: Boolean,
      tags: List<String>,
      groceries: List<String>,
      guests: Map<String, Boolean>,
      startsAt: Calendar,
      endsAt: Calendar
  ) : this(
      id,
      title,
      description,
      location,
      public,
      tags,
      groceries,
      guests,
      buildTimestamp(startsAt),
      buildTimestamp(endsAt))
}
