package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class Database(tables: Tables = TEST_TABLES) {
  private val db = Firebase.firestore
  private val events = db.collection(tables.EVENTS)
  private val accounts = db.collection(tables.ACCOUNTS)
  private val profilePictures = Firebase.storage.reference.child(tables.PROFILE_PICTURES)

  val eventManager = ChimpagneEventManager(this, events)
  val accountManager = ChimpagneAccountManager(this, accounts, profilePictures)
}

interface Tables {
  val EVENTS: String
  val ACCOUNTS: String
  val PROFILE_PICTURES: String
}
object PUBLIC_TABLES : Tables {
  override val EVENTS = "events"
  override val ACCOUNTS = "accounts"
  override val PROFILE_PICTURES = "profilePictures"
}

object TEST_TABLES : Tables {
  override val EVENTS = "testEvents"
  override val ACCOUNTS = "testAccounts"
  override val PROFILE_PICTURES = "testProfilePictures"
}
