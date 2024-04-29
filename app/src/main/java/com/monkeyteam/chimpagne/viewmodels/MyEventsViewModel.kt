package com.monkeyteam.chimpagne.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyEventsViewModel(
    database: Database,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(MyEventsUIState())
  val uiState: StateFlow<MyEventsUIState> = _uiState

  private val accountManager = database.accountManager

  init {
    fetchMyEvents(onSuccess, onFailure)
  }

  private fun fetchMyEvents(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(loading = true)
    viewModelScope.launch {
      accountManager.getAllOfMyEvents(
          { createdEvents, joinedEvents ->
            _uiState.value =
                _uiState.value.copy(
                    createdEvents = createdEvents.associateBy { event -> event.id },
                    joinedEvents = joinedEvents.associateBy { event -> event.id })
            _uiState.value = _uiState.value.copy(loading = false)
            onSuccess()
          },
          {
            Log.d("MY EVENTS", it.toString())
            _uiState.value = _uiState.value.copy(loading = false)
            onFailure(it)
          })
    }
  }
}

data class MyEventsUIState(
    val createdEvents: Map<String, ChimpagneEvent> = emptyMap(),
    val joinedEvents: Map<String, ChimpagneEvent> = emptyMap(),
    val loading: Boolean = false
)

class MyEventsViewModelFactory(private val database: Database) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return MyEventsViewModel(database) as T
  }
}
