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
import kotlinx.coroutines.launch

class EventViewModel(
    eventID: String? = null,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(EventUIState())
  private val fireBaseDB = Database()

  init {
    if (eventID != null) {
      fetchEvent(eventID, onSuccess, onFailure)
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
              _uiState.value = _uiState.value.copy(startsAtCalendarDate = it.startAt)
              _uiState.value = _uiState.value.copy(endsAtCalendarDate = it.endsAt)
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
      startsAtTimestamp: Timestamp = Timestamp(_uiState.value.startsAtCalendarDate.time),
      endsAtTimestamp: Timestamp = Timestamp(_uiState.value.endsAtCalendarDate.time)
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
      val newEventId =
          fireBaseDB.eventManager.registerEvent(
              createChimpagneEvent(id = "", guests = emptyMap()),
              { onSuccess() },
              {
                Log.d("CREATE AN EVENT", "Error : ", it)
                onFailure(it)
              })
      _uiState.value = _uiState.value.copy(id = newEventId)
    }
  }

  fun updateTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.updateEvent(
          createChimpagneEvent(),
          { fetchEvent(_uiState.value.id, onSuccess, onFailure) },
          {
            Log.d("UPDATE AN EVENT", "Error : ", it)
            onFailure(it)
          })
    }
  }

  fun deleteTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      fireBaseDB.eventManager.deleteEvent(
          _uiState.value.id,
          {
            _uiState.value = EventUIState()
            onSuccess()
          },
          {
            Log.d("DELETE AN EVENT", "Error : ", it)
            onFailure(it)
          })
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

  fun getEventGuestSet(): Set<String> {
    return _uiState.value.guests.keys
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

  fun getEventId(): String {
    return _uiState.value.id
  }

  fun getEventTitle(): String {
    return _uiState.value.title
  }

  fun updateEventTitle(newTitle: String) {
    _uiState.value = _uiState.value.copy(title = newTitle)
  }

  fun getEventDescription(): String {
    return _uiState.value.description
  }

  fun updateEventDescription(newDescription: String) {
    _uiState.value = _uiState.value.copy(description = newDescription)
  }

  fun getEventLocationSearchField(): String {
    return _uiState.value.locationSearchField
  }

  fun getAllPossibleLocationsList(): List<Location> {
    return _uiState.value.possibleLocationsList
  }

  fun updateEventLocationSearchField(newLocationSearchField: String) {
    _uiState.value = _uiState.value.copy(locationSearchField = newLocationSearchField)

    convertNameToLocation(
        newLocationSearchField,
        { _uiState.value = _uiState.value.copy(possibleLocationsList = it) })
  }

  fun getEventLocation(): Location {
    return _uiState.value.location
  }

  fun updateEventLocation(newLocation: Location) {
    _uiState.value = _uiState.value.copy(location = newLocation)
  }

  fun getEventPublicity(): Boolean {
    return _uiState.value.isPublic
  }

  fun updateEventPublicity(newIsPublic: Boolean) {
    _uiState.value = _uiState.value.copy(isPublic = newIsPublic)
  }

  fun getEventTags(): List<String> {
    return _uiState.value.tags
  }

  fun updateEventTags(newTags: List<String>) {
    _uiState.value = _uiState.value.copy(tags = newTags)
  }

  fun getEventStartCalendarDate(): Calendar {
    return _uiState.value.startsAtCalendarDate
  }

  fun updateEventStartCalendarDate(newStartCalendarDate: Calendar) {
    _uiState.value = _uiState.value.copy(startsAtCalendarDate = newStartCalendarDate)
  }

  fun getEventEndCalendarDate(): Calendar {
    return _uiState.value.endsAtCalendarDate
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
    val endsAtCalendarDate: Calendar = Calendar.getInstance()
)
