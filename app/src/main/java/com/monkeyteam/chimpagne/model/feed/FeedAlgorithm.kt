package com.monkeyteam.chimpagne.model.feed

import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location

// 50 as a reasonable value
const val N_CLOSEST = 50

fun getClosestNEvent(
    li: List<ChimpagneEvent>,
    myLocation: Location,
): List<ChimpagneEvent> {
  val sortedEvents =
      li.sortedBy { event ->
        val eventLocation = event.location
        myLocation.distanceTo(eventLocation)
      }

  return sortedEvents.take(N_CLOSEST)
}
