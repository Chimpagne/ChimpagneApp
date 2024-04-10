package com.monkeyteam.chimpagne.model.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.Location.Companion.convertNameToLocation
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(eventID: String? = null) : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(EventUIState())
  val uiState: StateFlow<EventUIState> = _uiState
  private val fireBaseDB = Database()

  init {
    if (eventID != null) {
      fetchEvent(eventID)
    }
  }

  private fun fetchEvent(
    id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}
  ) {
    viewModelScope.launch {
      fireBaseDB.eventManager.getEventById(id, {
        if (it != null) {
          _uiState.value = EventUIState(
            it.id,
            it.title,
            it.description,
            it.location,
            emptyList(),
            it.location.name,
            it.public,
            it.tags,
            it.guests,
            it.startAt,
            it.endsAt
          )
          onSuccess()
        } else {
          Log.d("FETCHING AN EVENT WITH ID", "Error : no such event exists")
        }
      }, {
        Log.d("FETCHING AN EVENT WITH ID", "Error : ", it)
        onFailure(it)
      })
    }
  }

  private fun buildChimpagneEvent(): ChimpagneEvent {
    return ChimpagneEvent(
      _uiState.value.id,
      _uiState.value.title,
      _uiState.value.description,
      _uiState.value.location,
      _uiState.value.isPublic,
      _uiState.value.tags,
      _uiState.value.guests,
      _uiState.value.startsAtCalendarDate,
      _uiState.value.endsAtCalendarDate
    )
  }

  fun createTheEvent(onSuccess: (id: String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      fireBaseDB.eventManager.registerEvent(buildChimpagneEvent(),
        {
//          fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure)
          _uiState.value = _uiState.value.copy(loading = false)
          onSuccess(it)
        },
        {
          Log.d("CREATE AN EVENT", "Error : ", it)
          _uiState.value = _uiState.value.copy(loading = false)
          onFailure(it)
        })
    }
  }

  fun updateTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.updateEvent(buildChimpagneEvent(), onSuccess) {
        Log.d("UPDATE AN EVENT", "Error : ", it)
        onFailure(it)
      }
    }
  }

  fun deleteTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.deleteEvent(uiState.value.id, onSuccess) {
        Log.d("DELETE AN EVENT", "Error : ", it)
        onFailure(it)
      }
    }
  }

// Seems to be not the good place for this
//  fun addGuestToTheEvent(
//    guestId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}
//  ) {
//    viewModelScope.launch {
//      fireBaseDB.eventManager.addGuestToEvent(buildChimpagneEvent(),
//        guestId,
//        { fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure) },
//        {
//          Log.d("ADD GUEST TO EVENT", "Error : ", it)
//          onFailure(it)
//        })
//    }
//  }
//
//  fun removeGuestFromTheEvent(
//    guestId: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}
//  ) {
//    viewModelScope.launch {
//      fireBaseDB.eventManager.removeGuestFromEvent(buildChimpagneEvent(),
//        guestId,
//        { fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure) },
//        {
//          Log.d("REMOVE GUEST TO EVENT", "Error : ", it)
//          onFailure(it)
//        })
//    }
//  }


  fun updateEventTitle(newTitle: String) {
    _uiState.value = _uiState.value.copy(title = newTitle)
  }

  fun updateEventDescription(newDescription: String) {
    _uiState.value = _uiState.value.copy(description = newDescription)
  }

  fun updateEventLocationSearchField(newLocationSearchField: String) {
    _uiState.value = _uiState.value.copy(locationSearchField = newLocationSearchField)

    convertNameToLocation(
      newLocationSearchField,
      { _uiState.value = _uiState.value.copy(possibleLocationsList = it) })
  }

  fun updateEventLocation(newLocation: Location) {
    _uiState.value = _uiState.value.copy(location = newLocation)
  }

  fun updateEventPublicity(newIsPublic: Boolean) {
    _uiState.value = _uiState.value.copy(isPublic = newIsPublic)
  }

  fun updateEventTags(newTags: List<String>) {
    _uiState.value = _uiState.value.copy(tags = newTags)
  }


  fun updateEventStartCalendarDate(newStartCalendarDate: Calendar) {
    _uiState.value = _uiState.value.copy(startsAtCalendarDate = newStartCalendarDate)
  }

  fun updateEventEndCalendarDate(newEndCalendarDate: Calendar) {
    _uiState.value = _uiState.value.copy(endsAtCalendarDate = newEndCalendarDate)
  }
}

data class EventUIState(
  val id: String = "",
  val title: String = "",
  val description: String = "",
  val location: Location = Location("default"),
  val possibleLocationsList: List<Location> = emptyList(),
  val locationSearchField: String = "",
  val isPublic: Boolean = false,
  val tags: List<String> = emptyList(),
  val guests: Map<String, Boolean> = emptyMap(),
  val startsAtCalendarDate: Calendar = Calendar.getInstance(),
  val endsAtCalendarDate: Calendar = Calendar.getInstance(),

  val loading: Boolean = false
)
