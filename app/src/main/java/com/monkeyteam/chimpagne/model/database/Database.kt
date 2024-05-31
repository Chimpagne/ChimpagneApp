package com.monkeyteam.chimpagne.model.database

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.monkeyteam.chimpagne.model.utils.internetAccessListener

// context != null iff not running a test
class Database(tables: Tables = TEST_TABLES, val context: Context? = null) {
  private val db = Firebase.firestore
  private val events = db.collection(tables.EVENTS)
  private val accounts = db.collection(tables.ACCOUNTS)
  private val profilePictures = Firebase.storage.reference.child(tables.PROFILE_PICTURES)
  private val eventPictures = Firebase.storage.reference.child(tables.EVENT_PICTURES)

  val eventManager = ChimpagneEventManager(this, events, eventPictures)
  val accountManager = ChimpagneAccountManager(this, accounts, profilePictures)

  var connected = false
    private set

  init {
    if (context == null) { // context == null means we are in a test and thus have Internet
      connected = true
    } else {
      internetAccessListener(context, onAvailable = { connected = true }, onLost = { connected = false })
    }
  }
}

interface Tables {
  val EVENTS: String
  val ACCOUNTS: String
  val PROFILE_PICTURES: String
  val EVENT_PICTURES: String
}

/** Tables used in production */
object PRODUCTION_TABLES : Tables {
  override val EVENTS = "events"
  override val ACCOUNTS = "accounts"
  override val PROFILE_PICTURES = "profilePictures"
  override val EVENT_PICTURES = "eventPictures"
}

/** Tables used for testing */
object TEST_TABLES : Tables {
  override val EVENTS = "testevents"
  override val ACCOUNTS = "testAccounts"
  override val PROFILE_PICTURES = "testProfilePictures"
  override val EVENT_PICTURES = "testEventPictures"
}
