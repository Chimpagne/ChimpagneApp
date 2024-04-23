package com.monkeyteam.chimpagne.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventManager
import com.monkeyteam.chimpagne.model.database.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel(
    private val eventManager: ChimpagneEventManager = Database.instance.eventManager,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(MyEventsUIState())
  val uiState: StateFlow<MyEventsUIState> = _uiState

  init {
    fetchMyEvents(onSuccess, onFailure)
  }

  private fun fetchMyEvents(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      /*TODO ADD DATABASE FUNCTION*/

      _uiState.value =
          _uiState.value.copy(
              createdEvents =
                  mapOf(
                      Pair("000", ChimpagneEvent(id = "000", title = "party 1")),
                      Pair("001", ChimpagneEvent(id = "000", title = "party 2")),
                      Pair("002", ChimpagneEvent(id = "000", title = "party 3")),
                      Pair("003", ChimpagneEvent(id = "000", title = "party 4")),
                      Pair("004", ChimpagneEvent(id = "000", title = "party 5")),
                      Pair("005", ChimpagneEvent(id = "000", title = "party 6")),
                  ),
              joinedEvents =
                  mapOf(
                      Pair("000", ChimpagneEvent(id = "000", title = "party 1")),
                      Pair("001", ChimpagneEvent(id = "000", title = "party 2")),
                      Pair("002", ChimpagneEvent(id = "000", title = "party 3")),
                      Pair("003", ChimpagneEvent(id = "000", title = "party 4")),
                      Pair("004", ChimpagneEvent(id = "000", title = "party 5")),
                      Pair("005", ChimpagneEvent(id = "000", title = "party 6")),
                  ),
              loading = false)
    }
  }
}

data class MyEventsUIState(
    val createdEvents: Map<String, ChimpagneEvent> = emptyMap(),
    val joinedEvents: Map<String, ChimpagneEvent> = emptyMap(),
    val loading: Boolean = false
)
