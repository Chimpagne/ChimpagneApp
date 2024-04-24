package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventId
import com.monkeyteam.chimpagne.model.database.ChimpagneRoles
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.containsTagsFilter
import com.monkeyteam.chimpagne.model.database.happensOnThisDateFilter
import com.monkeyteam.chimpagne.model.database.onlyPublicFilter
import com.monkeyteam.chimpagne.model.location.Location
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindEventsViewModel(database: Database) : ViewModel() {

  private val eventManager = database.eventManager
  private val accountManager = database.accountManager

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FindEventsUIState())
  val uiState: StateFlow<FindEventsUIState> = _uiState

  fun fetchEvents(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      var filter =
          Filter.and(onlyPublicFilter(), happensOnThisDateFilter(_uiState.value.selectedDate))
      if (_uiState.value.selectedTags.isNotEmpty())
          filter = Filter.and(filter, containsTagsFilter(_uiState.value.selectedTags))

      if (_uiState.value.selectedLocation != null) {
        eventManager.getAllEventsByFilterAroundLocation(
            _uiState.value.selectedLocation!!,
            _uiState.value.radiusAroundLocationInM,
            {
              _uiState.value =
                  _uiState.value.copy(
                      events = it.associateBy { event -> event.id }, loading = false)
              onSuccess()
            },
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
    _uiState.value = _uiState.value.copy(selectedTags = newTagList.distinct())
  }

  fun updateSelectedDate(newQuery: Calendar) {
    _uiState.value = _uiState.value.copy(selectedDate = newQuery)
  }

  fun joinEvent(eventId: ChimpagneEventId, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    accountManager.joinEvent(eventId, ChimpagneRoles.GUEST, onSuccess, onFailure)
  }
}

data class FindEventsUIState(
    val events: Map<String, ChimpagneEvent> = emptyMap(),
    val selectedLocation: Location? = null,
    val radiusAroundLocationInM: Double = 0.0,
    val selectedTags: List<String> = emptyList(),
    val selectedDate: Calendar = Calendar.getInstance(),
    val loading: Boolean = false
)

class FindEventsViewModelFactory(private val database: Database) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return FindEventsViewModel(database) as T
  }
}
