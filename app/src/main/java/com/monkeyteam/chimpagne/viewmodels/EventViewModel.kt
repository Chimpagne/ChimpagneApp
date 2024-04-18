package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventManager
import com.monkeyteam.chimpagne.model.database.ChimpagneSuppliesManager
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(
    eventID: String? = null,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {},
    private val eventManager: ChimpagneEventManager = Database.instance.eventManager,
    private val supplyManager: ChimpagneSuppliesManager = Database.instance.suppliesManager
) : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(EventUIState())
  val uiState: StateFlow<EventUIState> = _uiState

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
                      it.supplies,
                      it.guests,
                      it.startsAt(),
                      it.endsAt())
              onSuccess()
              _uiState.value = _uiState.value.copy(loading = false)
            } else {
              Log.d("FETCHING AN EVENT WITH ID", "Error : no such event exists")
              _uiState.value = _uiState.value.copy(loading = false)
            }
          }
      ) {
          Log.d("FETCHING AN EVENT WITH ID", "Error : ", it)
          _uiState.value = _uiState.value.copy(loading = false)
          onFailure(it)
      }
    }
  }

  private fun buildChimpagneEvent(): ChimpagneEvent {
    return ChimpagneEvent(
        _uiState.value.id,
        _uiState.value.title,
        _uiState.value.description,
        _uiState.value.location,
        _uiState.value.public,
        _uiState.value.tags,
        _uiState.value.supplies,
        _uiState.value.guests,
        _uiState.value.startsAtCalendarDate,
        _uiState.value.endsAtCalendarDate)
  }

  fun createTheEvent(onSuccess: (id: String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.registerEvent(
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

  fun addGuestToTheEvent(
      guestId: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.addGuestToEvent(
          buildChimpagneEvent(),
          guestId,
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
            Log.d("ADD GUEST TO EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }
    fun registerSupply(
        onSuccess: (supply: ChimpagneSupply) -> Unit = {},
        onFailure: (Exception) -> Unit = {},
        supply: ChimpagneSupply
    ) {
        _uiState.value = _uiState.value.copy(loading = true)
        viewModelScope.launch {
            supplyManager.registerSupply(
                supply,
                {
                    _uiState.value = _uiState.value.copy(id = it)
                    _uiState.value = _uiState.value.copy(loading = false)
                    supply.id = it
                    onSuccess(supply)
                },
                {
                    Log.d("register supply", "Error : ", it)
                    _uiState.value = _uiState.value.copy(loading = false)
                    onFailure(it)
                })
        }
    }

    fun deleteSupply(supplyId: String, onSuccess: () -> Unit) {
        _uiState.value = _uiState.value.copy(loading = true)
        viewModelScope.launch {
            supplyManager.deleteSupply(
                _uiState.value.id,
                onSuccess={
                    _uiState.value = EventUIState()
                    _uiState.value = _uiState.value.copy(loading = false)
                    onSuccess()
                          },
                onFailure={
                    Log.d("DELETE supply", "Error : ", it)
                    _uiState.value = _uiState.value.copy(loading = false)
                })
        }
    }

    fun addSupplyToEvent(
        supplyId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        _uiState.value = _uiState.value.copy(loading = true)
        viewModelScope.launch {
            eventManager.addSupplyToEvent(
                buildChimpagneEvent(),
                supplyId,
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
                    _uiState.value = _uiState.value.copy(loading = false)
                    onFailure(it)
                })
        }
    }

  fun removeGuestFromTheEvent(
      guestId: String,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.removeGuestFromEvent(
          buildChimpagneEvent(),
          guestId,
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
            Log.d("REMOVE GUEST TO EVENT", "Error : ", it)
            _uiState.value = _uiState.value.copy(loading = false)
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
}

data class EventUIState(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: Location = Location(),
    val public: Boolean = false,
    val tags: List<String> = emptyList(),
    var supplies: List<String> = emptyList(),
    val guests: Map<String, Boolean> = emptyMap(),
    val startsAtCalendarDate: Calendar = Calendar.getInstance(),
    val endsAtCalendarDate: Calendar = Calendar.getInstance(),
    val loading: Boolean = false
) {
}
