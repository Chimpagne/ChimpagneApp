package com.monkeyteam.chimpagne.model.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.containsTagsFilter
import com.monkeyteam.chimpagne.model.database.happensOnThisDateFilter
import com.monkeyteam.chimpagne.model.database.onlyPublicFilter
import com.monkeyteam.chimpagne.model.location.Location
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindEventsViewModel : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FindEventsUIState())
  val uiState: StateFlow<FindEventsUIState> = _uiState
  private val eventManager = Database().eventManager

  init {
    //    viewModelScope.launch {
    //      fireBaseDB.eventManager.getAllEvents(
    //          { _uiState.value = _uiState.value.copy(listOfEvents = it) },
    //          { Log.d("FETCHING ALL EVENTS", "Error : ", it) })
    //    }
  }

  fun fetchEvents(onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      var filter =
//          Filter.and(onlyPublicFilter())
          Filter.and(onlyPublicFilter(), happensOnThisDateFilter(_uiState.value.selectedDate))
      if (_uiState.value.tags.isNotEmpty())
          filter = Filter.and(filter, containsTagsFilter(_uiState.value.tags))

      if (_uiState.value.selectedLocation != null) {
        eventManager.getAllEventsByFilterAroundLocation(
            _uiState.value.selectedLocation!!,
            _uiState.value.radiusAroundLocationInM,
            {
              val monkey = it.associateBy { event -> event.id }
              println(monkey)
              _uiState.value = _uiState.value.copy(events = it.associateBy { event -> event.id }, loading = false) },
            {
              Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
              _uiState.value = _uiState.value.copy(loading = false)
              onFailure(it)
            },
            filter)
      }
    }
  }

  fun updateSelectedLocation(location: Location) {
    _uiState.value = _uiState.value.copy(selectedLocation = location)
  }

  fun updateLocationSearchRadius(newRadiusInM: Double) {
    _uiState.value = _uiState.value.copy(radiusAroundLocationInM = newRadiusInM)
  }

  fun updateTags(newTagList: List<String>) {
    _uiState.value = _uiState.value.copy(tags = newTagList)
  }

  fun updateSelectedDate(newQuery: Calendar) {
    _uiState.value = _uiState.value.copy(selectedDate = newQuery)
  }
}

data class FindEventsUIState(
  val events: Map<String, ChimpagneEvent> = emptyMap(),
  val selectedLocation: Location? = Location("Balelec", 46.519144, 6.566804),
  val radiusAroundLocationInM: Double = 0.0,
  val tags: List<String> = emptyList(),
  val selectedDate: Calendar = Calendar.getInstance(),

  val loading: Boolean = false
)
