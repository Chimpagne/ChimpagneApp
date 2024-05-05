package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneAccountUID
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
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
    private var eventID: String? = null,
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
    onSuccess()
  }

  /* THIS MUST BE CALLED IN MAIN ACTIVITY ON TRANSITION TO THE SCREEN THAT USES THE VIEW MODEL */
  fun fetchEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (eventID != null) {
      _uiState.value = _uiState.value.copy(loading = true)
      viewModelScope.launch {
        eventManager.getEventById(
            eventID!!,
            {
              if (it != null) {
                _uiState.value =
                    _uiState.value.copy(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        location = it.location,
                        public = it.public,
                        tags = it.tags,
                        guests = it.guests,
                        staffs  = it.staffs,
                        startsAtCalendarDate = it.startsAt(),
                        endsAtCalendarDate = it.endsAt(),
                        supplies = it.supplies,
                        parkingSpaces = it.parkingSpaces,
                        beds = it.beds,
                        ownerId = it.ownerId)
                _uiState.value =
                    _uiState.value.copy(
                        currentUserRole =
                            getRole(accountManager.currentUserAccount?.firebaseAuthUID ?: ""))
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
    } else {
      _uiState.value =
          EventUIState(
              ownerId = accountManager.currentUserAccount?.firebaseAuthUID ?: "",
              currentUserRole = ChimpagneRole.OWNER)
    }
  }

  fun fetchAccounts(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    if (eventID != null) {
      _uiState.value = _uiState.value.copy(loading = true)
      viewModelScope.launch {
        accountManager.getAccounts(
            setOf(_uiState.value.ownerId) + _uiState.value.guests.keys + _uiState.value.staffs.keys,
            {
              _uiState.value = _uiState.value.copy(accounts = it)
              onSuccess()
              _uiState.value = _uiState.value.copy(loading = false)
            }) {
              Log.d("FETCHING EVENT ACCOUNTS", "Error : ", it)
              _uiState.value = _uiState.value.copy(loading = false)
              onFailure(it)
            }
      }
    }
  }

  private fun buildChimpagneEvent(): ChimpagneEvent {
    return ChimpagneEvent(
        id = _uiState.value.id,
        title = _uiState.value.title,
        description = _uiState.value.description,
        location = _uiState.value.location,
        public = _uiState.value.public,
        tags = _uiState.value.tags,
        guests = _uiState.value.guests,
        staffs = _uiState.value.staffs,
        startsAt = _uiState.value.startsAtCalendarDate,
        endsAt = _uiState.value.endsAtCalendarDate,
        ownerId = _uiState.value.ownerId,
        supplies = _uiState.value.supplies,
        parkingSpaces = _uiState.value.parkingSpaces,
        beds = _uiState.value.beds)
  }

  fun createTheEvent(onSuccess: (id: String) -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      eventManager.createEvent(
          buildChimpagneEvent(),
          {
            _uiState.value = _uiState.value.copy(id = it)
            eventID = _uiState.value.id
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

  fun joinTheEvent(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      accountManager.joinEvent(
          _uiState.value.id,
          ChimpagneRole.GUEST,
          {
            fetchEvent(
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

  fun addSupply(supply: ChimpagneSupply) {
    _uiState.value = _uiState.value.copy(supplies = _uiState.value.supplies + (supply.id to supply))
  }

  fun removeSupply(supplyId: ChimpagneSupplyId) {
    _uiState.value = _uiState.value.copy(supplies = _uiState.value.supplies - supplyId)
  }

  fun getRole(userUID: ChimpagneAccountUID): ChimpagneRole {
    return buildChimpagneEvent().getRole(userUID)
  }

  fun promoteGuestToStaff(uid: ChimpagneAccountUID) {
    _uiState.value =
        _uiState.value.copy(
            guests = _uiState.value.guests - uid, staffs = _uiState.value.staffs + (uid to true))
  }

  fun demoteStaffToGuest(uid: ChimpagneAccountUID) {
    _uiState.value =
        _uiState.value.copy(
            guests = _uiState.value.guests + (uid to true), staffs = _uiState.value.staffs - uid)
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
    // unmodifiable by the UI
    val ownerId: ChimpagneAccountUID = "",
    val currentUserRole: ChimpagneRole = ChimpagneRole.NOT_IN_EVENT,
    // OPTIONAL PARAMETER, MUST BE FETCH SEPARATELY
    val accounts: Map<String, ChimpagneAccount> = emptyMap(),
    val loading: Boolean = false
)

class EventViewModelFactory(private val eventID: String? = null, private val database: Database) :
    ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return EventViewModel(eventID, database) as T
  }
}
