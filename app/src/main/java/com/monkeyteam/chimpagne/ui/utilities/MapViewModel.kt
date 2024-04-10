package com.monkeyteam.chimpagne.ui.utilities

import com.monkeyteam.chimpagne.model.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel {

  private val _markers = MutableStateFlow<List<Location>>(emptyList())
  val markers: StateFlow<List<Location>> = _markers.asStateFlow()

  // TODO: add a marker should not use Location but an Event
  fun addMarker(location: Location) {
    val currentMarkers = _markers.value.toMutableList()
    currentMarkers.add(location)
    _markers.value = currentMarkers
  }

  fun removeMarker(location: Location) {
    val currentMarkers = _markers.value.toMutableList()
    currentMarkers.removeAll { it.name == location.name }
    _markers.value = currentMarkers
  }
}
