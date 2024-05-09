package com.monkeyteam.chimpagne.model.location

import android.location.Location

sealed class LocationState {
    object Idle : LocationState()
    object Searching : LocationState()
    data class Set(val location: Location) : LocationState()
    data class Error(val message: String) : LocationState()
}