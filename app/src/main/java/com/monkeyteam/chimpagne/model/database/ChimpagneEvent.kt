package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.ui.components.SupportedSocialMedia
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
    val supplies: Map<ChimpagneSupplyId, ChimpagneSupply> = mapOf(),
    val parkingSpaces: Int = 0,
    val beds: Int = 0,
    val image: String = "", // TODO: Add image
    val socialMediaLinks: Map<String, String> =
        SupportedSocialMedia.associateBy { it.platformName }.mapValues { it.value.chosenGroupUrl }
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

  fun getRole(userUID: ChimpagneAccountUID): ChimpagneRole {
    if (ownerId == userUID) return ChimpagneRole.OWNER
    if (staffs[userUID] == true) return ChimpagneRole.STAFF
    if (guests[userUID] == true) return ChimpagneRole.GUEST
    return ChimpagneRole.NOT_IN_EVENT
  }

  fun userSet(): Set<ChimpagneAccountUID> {
    return setOf(ownerId) + staffList() + guestList()
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
      supplies: Map<ChimpagneSupplyId, ChimpagneSupply> = mapOf(),
      parkingSpaces: Int,
      beds: Int,
      image: String,
      socialMediaLinks: Map<String, String>
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
      supplies,
      parkingSpaces,
      beds,
      image,
      socialMediaLinks)
}
