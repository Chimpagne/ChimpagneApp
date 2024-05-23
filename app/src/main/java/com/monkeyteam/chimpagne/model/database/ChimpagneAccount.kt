package com.monkeyteam.chimpagne.model.database

typealias ChimpagneAccountUID = String

data class ChimpagneAccount(
    val firebaseAuthUID: ChimpagneAccountUID = "",
    val firstName: String = "",
    val lastName: String = "",
    val joinedEvents: Map<ChimpagneEventId, Boolean> = hashMapOf()
)
