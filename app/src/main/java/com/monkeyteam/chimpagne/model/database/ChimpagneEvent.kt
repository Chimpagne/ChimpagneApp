package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import java.util.Calendar

typealias ChimpagneEventId = String

// https://stackoverflow.com/questions/39815117/add-an-item-to-a-list-in-firebase-database
data class ChimpagneEvent(
    val id: ChimpagneEventId = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location(),
    val public: Boolean = false,
    val tags: List<String> = listOf(),
    val guests: Map<ChimpagneAccountUID, Boolean> = hashMapOf(),
    val staffs: Map<ChimpagneAccountUID, Boolean> = hashMapOf(),
    val startsAtTimestamp: Timestamp = Timestamp.now(),
    val endsAtTimestamp: Timestamp = Timestamp.now(),
    val ownerId: ChimpagneAccountUID = "",
    val parkingSpaces: Int = 0,
    val beds: Int = 0
) {

  fun guestList(): Set<String> {
    return guests.keys
  }

  fun staffList(): Set<String> {
    return staffs.keys
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
      guests: Map<ChimpagneAccountUID, Boolean>,
      staffs: Map<ChimpagneAccountUID, Boolean>,
      startsAt: Calendar,
      endsAt: Calendar,
      ownerId: ChimpagneAccountUID,
      parkingSpaces: Int,
      beds: Int
  ) : this(
      id,
      title,
      description,
      location,
      public,
      tags,
      guests,
      staffs,
      buildTimestamp(startsAt),
      buildTimestamp(endsAt),
      ownerId,
      parkingSpaces,
      beds)
}
