package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Database {
  companion object {
    val instance = Database()
  }

  private val db = Firebase.firestore
  private val events = db.collection(TABLES.EVENTS)
  private val accounts = db.collection(TABLES.ACCOUNTS)

  val eventManager = ChimpagneEventManager(events)
  val accountManager = ChimpagneAccountManager(accounts)
}

private object TABLES {
  val EVENTS = "events"
  val ACCOUNTS = "accounts"
}
