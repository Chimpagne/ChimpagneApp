package com.monkeyteam.chimpagne.model.location

import android.location.Location

/** Used by the "Locate me" button, in both the main screen and the search screen. */
sealed class LocationState {
  object Idle : LocationState()

  object Searching : LocationState()

  data class Set(val location: Location) : LocationState()

  data class Error(val message: String) : LocationState()
}
