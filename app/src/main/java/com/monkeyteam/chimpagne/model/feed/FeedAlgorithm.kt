package com.monkeyteam.chimpagne.model.feed

import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location

val N_CLOSEST = 4

fun getClosestNEvent(
    li: List<ChimpagneEvent>,
    myLocation: Location,
): List<ChimpagneEvent> {
  if (li.size <= N_CLOSEST) return li

  val sortedEvents =
      li.sortedBy { event ->
        val eventLocation = event.location
        myLocation.distanceTo(eventLocation)
      }

  return sortedEvents.take(N_CLOSEST)
}
