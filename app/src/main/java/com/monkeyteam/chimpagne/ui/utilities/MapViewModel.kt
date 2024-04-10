package com.monkeyteam.chimpagne.ui.utilities

import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel {

  private val _markers = MutableStateFlow<List<ChimpagneEvent>>(emptyList())
  val markers: StateFlow<List<ChimpagneEvent>> = _markers.asStateFlow()

  // TODO: add a marker should not use Location but an Event
  fun addMarker(event: ChimpagneEvent) {
    val currentMarkers = _markers.value.toMutableList()
    currentMarkers.add(event)
    _markers.value = currentMarkers
  }

  fun removeMarker(event: ChimpagneEvent) {
    val currentMarkers = _markers.value.toMutableList()
    currentMarkers.removeAll { it.id == event.id }
    _markers.value = currentMarkers
  }

  fun getEventById(id: String): ChimpagneEvent? {
    return _markers.value.find { it.id == id }
  }

  fun clearMarkers() {
    _markers.value = emptyList()
  }
}
