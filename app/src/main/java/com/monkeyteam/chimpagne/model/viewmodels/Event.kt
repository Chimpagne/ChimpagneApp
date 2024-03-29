package com.monkeyteam.chimpagne.model.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.Location.Companion.convertNameToLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel(eventID: String? = null) : ViewModel() {
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
      id: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    viewModelScope.launch {
      fireBaseDB.eventManager.getEventById(
          id,
          {
            if (it != null) {
              _uiState.value = _uiState.value.copy(id = it.id)
              _uiState.value = _uiState.value.copy(title = it.title)
              _uiState.value = _uiState.value.copy(description = it.description)
              _uiState.value = _uiState.value.copy(location = it.location)
              _uiState.value = _uiState.value.copy(locationSearchField = it.location.name)
              _uiState.value = _uiState.value.copy(isPublic = it.isPublic)
              _uiState.value = _uiState.value.copy(tags = it.tags)
              _uiState.value = _uiState.value.copy(guests = it.guests)
              _uiState.value = _uiState.value.copy(startsAtTimestamp = it.startsAtTimestamp)
              _uiState.value = _uiState.value.copy(endsAtTimestamp = it.endsAtTimestamp)
              onSuccess()
            } else {
              Log.d("FETCHING AN EVENT WITH ID", "Error : no such event exists")
            }
          },
          {
            Log.d("FETCHING AN EVENT WITH ID", "Error : ", it)
            onFailure(it)
          })
    }
  }

  private fun createChimpagneEvent(
      id: String = _uiState.value.id,
      title: String = _uiState.value.title,
      description: String = _uiState.value.description,
      location: Location = _uiState.value.location,
      isPublic: Boolean = _uiState.value.isPublic,
      tags: List<String> = _uiState.value.tags,
      guests: Map<String, Boolean> = _uiState.value.guests,
      startsAtTimestamp: Timestamp = _uiState.value.startsAtTimestamp,
      endsAtTimestamp: Timestamp = _uiState.value.endsAtTimestamp
  ): ChimpagneEvent {
    return ChimpagneEvent(
        id,
        title,
        description,
        location,
        isPublic,
        tags,
        guests,
        startsAtTimestamp,
        endsAtTimestamp)
  }

  fun createTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.registerEvent(
          createChimpagneEvent(id = "", guests = emptyMap()),
          { fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure) },
          {
            Log.d("CREATE AN EVENT", "Error : ", it)
            onFailure(it)
          })
    }
  }

  fun updateTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.updateEvent(createChimpagneEvent(), onSuccess) {
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

  fun addGuestToTheEvent(
      guestId: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    viewModelScope.launch {
      fireBaseDB.eventManager.addGuestToEvent(
          createChimpagneEvent(),
          guestId,
          { fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure) },
          {
            Log.d("ADD GUEST TO EVENT", "Error : ", it)
            onFailure(it)
          })
    }
  }

  fun removeGuestFromTheEvent(
      guestId: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    viewModelScope.launch {
      fireBaseDB.eventManager.removeGuestFromEvent(
          createChimpagneEvent(),
          guestId,
          { fetchEvent(id = _uiState.value.id, onSuccess = onSuccess, onFailure = onFailure) },
          {
            Log.d("REMOVE GUEST TO EVENT", "Error : ", it)
            onFailure(it)
          })
    }
  }

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

  fun updateEventStartTime(newStartTime: Timestamp) {
    _uiState.value = _uiState.value.copy(startsAtTimestamp = newStartTime)
  }

  fun updateEventEndTime(newEndTime: Timestamp) {
    _uiState.value = _uiState.value.copy(endsAtTimestamp = newEndTime)
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
    val startsAtTimestamp: Timestamp = Timestamp.now(),
    val endsAtTimestamp: Timestamp = Timestamp.now()
)
