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
  private val groceries = db.collection(TABLES.GROCERIES)
  val eventManager = ChimpagneEventManager(events)
  val accountManager = ChimpagneAccountManager(accounts)
  val groceryManager = ChimpagneGroceryItemManager(groceries)
}

private object TABLES {
  val EVENTS = "events"
  val ACCOUNTS = "accounts"
  val GROCERIES = "groceries"
}
