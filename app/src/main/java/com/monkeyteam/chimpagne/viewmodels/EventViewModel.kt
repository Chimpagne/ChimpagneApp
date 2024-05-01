package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventId
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.ChimpagneSupplyId
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    eventID: String? = null,
    database: Database,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {},
) : ViewModel() {

  private val eventManager = database.eventManager
  private val accountManager = database.accountManager

  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(EventUIState())
  val uiState: StateFlow<EventUIState> = _uiState

  init {
    if (eventID != null) {
      fetchEvent(eventID, onSuccess, onFailure)
    }
  }

  private fun fetchEvent(
      id: ChimpagneEventId,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.getEventById(
          id,
          {
            if (it != null) {
              _uiState.value =
                  EventUIState(
                      it.id,
                      it.title,
                      it.description,
                      it.location,
                      it.public,
                      it.tags,
                      it.guests,
                      it.staffs,
                      it.startsAt(),
                      it.endsAt(),
                      it.supplies,
                      it.parkingSpaces,
                      it.beds)
              onSuccess()
              _uiState.value = _uiState.value.copy(loading = false)
            } else {
              Log.d("FETCHING AN EVENT WITH ID", "Error : no such event exists")
              _uiState.value = _uiState.value.copy(loading = false)
            }
          },
          {
            Log.d("FETCHING AN EVENT WITH ID", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun buildChimpagneEvent(): ChimpagneEvent {
    return ChimpagneEvent(
        _uiState.value.id,
        _uiState.value.title,
        _uiState.value.description,
        _uiState.value.location,
        _uiState.value.public,
        _uiState.value.tags,
        _uiState.value.guests,
        _uiState.value.staffs,
        _uiState.value.startsAtCalendarDate,
        _uiState.value.endsAtCalendarDate,
        "", // This will be handled in the database
        _uiState.value.supplies,
        _uiState.value.parkingSpaces,
        _uiState.value.beds)
  }

  fun createTheEvent(onSuccess: (id: String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.createEvent(
          buildChimpagneEvent(),
          {
            _uiState.value = _uiState.value.copy(id = it)
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
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.updateEvent(
          buildChimpagneEvent(),
          {
            _uiState.value = _uiState.value.copy(loading = false)
            onSuccess()
          },
          {
            Log.d("UPDATE AN EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun deleteTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.deleteEvent(
          _uiState.value.id,
          {
            _uiState.value = EventUIState()
            _uiState.value = _uiState.value.copy(loading = false)
            onSuccess()
          },
          {
            Log.d("DELETE AN EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun joinEvent(
      role: ChimpagneRole,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      accountManager.joinEvent(
          _uiState.value.id,
          role,
          {
            fetchEvent(
                id = _uiState.value.id,
                onSuccess = {
                  _uiState.value = _uiState.value.copy(loading = false)
                  onSuccess()
                },
                onFailure = {
                  _uiState.value = _uiState.value.copy(loading = false)
                  onFailure(it)
                })
          },
          {
            Log.d("ADD MONKEY TO EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun leaveTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      accountManager.leaveEvent(
          _uiState.value.id,
          {
            fetchEvent(
                id = _uiState.value.id,
                onSuccess = {
                  _uiState.value = _uiState.value.copy(loading = false)
                  onSuccess()
                },
                onFailure = {
                  _uiState.value = _uiState.value.copy(loading = false)
                  onFailure(it)
                })
          },
          {
            Log.d("REMOVE MONKEY FROM EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }

  fun updateEventTitle(newTitle: String) {
    _uiState.value = _uiState.value.copy(title = newTitle)
  }

  fun updateParkingSpaces(newParkingSpaces: Int) {
    _uiState.value = _uiState.value.copy(parkingSpaces = newParkingSpaces)
  }

  fun updateBeds(newBeds: Int) {
    _uiState.value = _uiState.value.copy(beds = newBeds)
  }

  fun updateEventDescription(newDescription: String) {
    _uiState.value = _uiState.value.copy(description = newDescription)
  }

  fun updateEventLocation(newLocation: Location) {
    _uiState.value = _uiState.value.copy(location = newLocation)
  }

  fun updateEventPublicity(newPublic: Boolean) {
    _uiState.value = _uiState.value.copy(public = newPublic)
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

  fun updateEventSupplies(newSupplies: Map<ChimpagneSupplyId, ChimpagneSupply>) {
    _uiState.value = _uiState.value.copy(supplies = newSupplies)
  }

  fun addSuply(supply: ChimpagneSupply) {
    _uiState.value = _uiState.value.copy(supplies = _uiState.value.supplies + (supply.id to supply))
  }

  fun removeSupply(supplyId: ChimpagneSupplyId) {
    _uiState.value = _uiState.value.copy(supplies = _uiState.value.supplies - supplyId)
  }
}

data class EventUIState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location(),
    val public: Boolean = false,
    val tags: List<String> = emptyList(),
    val guests: Map<String, Boolean> = emptyMap(),
    val staffs: Map<String, Boolean> = emptyMap(),
    val startsAtCalendarDate: Calendar = Calendar.getInstance(),
    val endsAtCalendarDate: Calendar = Calendar.getInstance(),
    val supplies: Map<ChimpagneSupplyId, ChimpagneSupply> = mapOf(),
    val parkingSpaces: Int = 0,
    val beds: Int = 0,
    val loading: Boolean = false,
)

class EventViewModelFactory(private val eventID: String? = null, private val database: Database) :
    ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return EventViewModel(eventID, database) as T
  }
}
