package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventId
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
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

  private val _uiState = MutableStateFlow(FindEventsUIState())
  val uiState: StateFlow<FindEventsUIState> = _uiState

  fun fetchEvents(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (_uiState.value.loading) return
    setLoading(true)
    viewModelScope.launch {
      try {
        var filter =
            Filter.and(onlyPublicFilter(), happensOnThisDateFilter(_uiState.value.selectedDate))
        if (_uiState.value.selectedTags.isNotEmpty())
            filter = Filter.and(filter, containsTagsFilter(_uiState.value.selectedTags))

        if (_uiState.value.selectedLocation != null) {
          eventManager.getAllEventsByFilterAroundLocation(
              _uiState.value.selectedLocation!!,
              _uiState.value.radiusAroundLocationInM,
              {
                _uiState.value = _uiState.value.copy(events = it.associateBy { event -> event.id })
                if (it.isEmpty()) {
                  Log.d("FETCHING EVENTS BY LOCATION QUERY", "No events found")
                  setLoading(false)
                  onFailure(Exception("No events found"))
                } else {
                  // DO NO FORGET TO SETLOADING TO FALSE AFTER SUCCESS (where function is called)
                  // (AFTER UI RECOMPOSITION)
                  Log.d("FETCHING EVENTS BY LOCATION QUERY", "Success")
                  onSuccess()
                }
              },
              {
                Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
                setLoading(false)
                onFailure(it)
              },
              filter)
        } else {
          setLoading(false)
        }
      } catch (e: Exception) {
        setLoading(false)
        onFailure(e)
      }
    }
  }

    fun fetchAroundLocation(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}){
        println("FETCHING AROUND LOCATION");
        eventManager.getAllEventsByFilterAroundLocation(
            _uiState.value.selectedLocation!!,
                99999.0,
            {
                _uiState.value = _uiState.value.copy(events = it.associateBy { event -> event.id })
                if (it.isEmpty()) {
                    Log.d("FETCHING EVENTS BY LOCATION QUERY", "No events found")
                    setLoading(false)
                    onFailure(Exception("No events found"))
                } else {
                    // DO NO FORGET TO SETLOADING TO FALSE AFTER SUCCESS (where function is called)
                    // (AFTER UI RECOMPOSITION)
                    Log.d("FETCHING EVENTS BY LOCATION QUERY", "Success")
                    onSuccess()
                }
            },
            {
                Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
                setLoading(false)
                onFailure(it)
            })
    }

  fun fetchEvent(
      id: ChimpagneEventId,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    if (_uiState.value.loading) return
    setLoading(true)
    viewModelScope.launch {
      try {
        eventManager.getEventById(
            id,
            {
              if (it != null) {
                _uiState.value = _uiState.value.copy(events = mapOf(it.id to it), loading = false)
                onSuccess()
              } else {
                Log.d("FETCHING AN EVENT WITH ID", "Error : no such event exists")
                setLoading(false)
              }
            },
            onFailure)
      } catch (e: Exception) {
        setLoading(false)
        onFailure(e)
      }
    }
  }

  fun setResultEvents(events: Map<ChimpagneEventId, ChimpagneEvent>) {
    _uiState.value = _uiState.value.copy(events = events)
  }

  fun eraseResults() {
    _uiState.value = _uiState.value.copy(events = emptyMap())
  }

  fun setLoading(loading: Boolean = true) {
    _uiState.value = _uiState.value.copy(loading = loading)
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

  fun joinEvent(
      eventId: ChimpagneEventId,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    _uiState.value = _uiState.value.copy(loading = true)
    accountManager.joinEvent(
        eventId,
        ChimpagneRole.GUEST,
        {
          _uiState.value = _uiState.value.copy(loading = false)
          onSuccess()
        },
        onFailure)
  }
}

data class FindEventsUIState(
    val events: Map<String, ChimpagneEvent> = emptyMap(),
    val selectedLocation: Location? = null,
    val radiusAroundLocationInM: Double = 1000.0,
    val selectedTags: List<String> = emptyList(),
    val selectedDate: Calendar = Calendar.getInstance(),
    val loading: Boolean = false
)

class FindEventsViewModelFactory(private val database: Database) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return FindEventsViewModel(database) as T
  }
}
