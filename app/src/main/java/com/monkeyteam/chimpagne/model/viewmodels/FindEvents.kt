package com.monkeyteam.chimpagne.model.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.containsTagsFilter
import com.monkeyteam.chimpagne.model.database.endsAfterFilter
import com.monkeyteam.chimpagne.model.database.endsBeforeFilter
import com.monkeyteam.chimpagne.model.database.isTitle
import com.monkeyteam.chimpagne.model.database.onlyPublic
import com.monkeyteam.chimpagne.model.database.startsAfterFilter
import com.monkeyteam.chimpagne.model.database.startsBeforeFilter
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.Location.Companion.convertNameToLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FindEventsViewModel : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FindEventsUIState())
  val uiState: StateFlow<FindEventsUIState> = _uiState
  private val fireBaseDB = Database()

  init {
    viewModelScope.launch {
      fireBaseDB.eventManager.getAllEvents(
          { _uiState.value = _uiState.value.copy(listOfEvents = it) },
          { Log.d("FETCHING ALL EVENTS", "Error : ", it) })
    }
  }

  private fun fetchAllEventsByQueries(onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      var filter = onlyPublic()
      if (_uiState.value.searchByExactTitle != "")
          filter = Filter.and(filter, isTitle(_uiState.value.searchByExactTitle))
      if (_uiState.value.searchByTags.isNotEmpty())
          filter = Filter.and(filter, containsTagsFilter(_uiState.value.searchByTags))
      if (_uiState.value.searchByStartsBefore != null)
          filter = Filter.and(filter, startsBeforeFilter(_uiState.value.searchByStartsBefore!!))
      if (_uiState.value.searchByStartsAfter != null)
          filter = Filter.and(filter, startsAfterFilter(_uiState.value.searchByStartsAfter!!))
      if (_uiState.value.searchByEndsBefore != null)
          filter = Filter.and(filter, endsBeforeFilter(_uiState.value.searchByEndsBefore!!))
      if (_uiState.value.searchByEndsAfter != null)
          filter = Filter.and(filter, endsAfterFilter(_uiState.value.searchByEndsAfter!!))

      if (_uiState.value.possibleLocationsList.isNotEmpty()) {
        fireBaseDB.eventManager.getAllEventsByFilterAroundLocation(
            _uiState.value.actualLocation,
            _uiState.value.radiusAroundLocationInM,
            { _uiState.value = _uiState.value.copy(listOfEvents = it) },
            {
              Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
              onFailure(it)
            },
            filter)
      } else {
        fireBaseDB.eventManager.getAllEventsByFilter(
            filter,
            { _uiState.value = _uiState.value.copy(listOfEvents = it) },
            {
              Log.d("FETCHING EVENTS BY WITHOUT LOCATION QUERY", "Error : ", it)
              onFailure(it)
            },
        )
      }
    }
  }

  // WARNING TITLE MUST BE EXACT, LEAVE IT EMPTY IF YOU DO NOT WANT TO LIMIT YOUR SEARCHES
  fun updateExactTitleQuery(newQuery: String, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByExactTitle = newQuery)
    fetchAllEventsByQueries(onFailure)
  }

  fun updateLocationSearchQuery(newQuery: String, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByLocation = newQuery)
    convertNameToLocation(
        _uiState.value.searchByLocation,
        {
          _uiState.value = _uiState.value.copy(possibleLocationsList = it)
          fetchAllEventsByQueries(onFailure)
        })
  }

  fun updateActualLocation(newLocation: Location, onFailure: (Exception) -> Unit = {}){
      _uiState.value = _uiState.value.copy(actualLocation = newLocation)
      _uiState.value = _uiState.value.copy(searchByLocation = newLocation.toString())
      fetchAllEventsByQueries(onFailure)
  }

  fun updateLocationSearchRadius(newRadiusInM: Double, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(radiusAroundLocationInM = newRadiusInM)
    fetchAllEventsByQueries(onFailure)
  }

  fun updateTagsQuery(newTagList: List<String>, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByTags = newTagList)
    fetchAllEventsByQueries(onFailure)
  }

  fun updateStartsBeforeQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByStartsBefore = newQuery)
    fetchAllEventsByQueries(onFailure)
  }

  fun updateStartsAfterQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByStartsAfter = newQuery)
    fetchAllEventsByQueries(onFailure)
  }

  fun updateEndsBeforeQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByEndsBefore = newQuery)
    fetchAllEventsByQueries(onFailure)
  }
  fun updateEndsAfterQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByEndsAfter = newQuery)
    fetchAllEventsByQueries(onFailure)
  }
}

data class FindEventsUIState(
    val listOfEvents: List<ChimpagneEvent> = emptyList(),
    val searchByExactTitle: String = "",
    val searchByLocation: String = "",
    val actualLocation: Location = Location(),
    val possibleLocationsList: List<Location> = emptyList(),
    val radiusAroundLocationInM: Double = 0.0,
    val searchByTags: List<String> = emptyList(),
    val searchByStartsBefore: Timestamp? = null,
    val searchByStartsAfter: Timestamp? = null,
    val searchByEndsBefore: Timestamp? = null,
    val searchByEndsAfter: Timestamp? = null,
)
