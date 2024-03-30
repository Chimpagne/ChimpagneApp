package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Database {
  companion object {
    val instance = Database()
  }

  private val db = Firebase.firestore
  private val events = db.collection(TABLES.EVENTS)

  val eventManager = ChimpagneEventManager(events)
}

private object TABLES {
  val EVENTS = "events"
}
