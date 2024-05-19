package com.monkeyteam.chimpagne.model.database

import com.monkeyteam.chimpagne.model.location.Location

typealias ChimpagneAccountUID = String

data class ChimpagneAccount(
    val firebaseAuthUID: ChimpagneAccountUID = "",
    val firstName: String = "",
    val lastName: String = "",
    val location: Location = Location(),
    val joinedEvents: Map<ChimpagneEventId, Boolean> = hashMapOf()
)

fun ChimpagneAccount.toPartialAccount(): ChimpagneAccount {
  return ChimpagneAccount(
      firebaseAuthUID = this.firebaseAuthUID, firstName = this.firstName, lastName = this.lastName)
}
