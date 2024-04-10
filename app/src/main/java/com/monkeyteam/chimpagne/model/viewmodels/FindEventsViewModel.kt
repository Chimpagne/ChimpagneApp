package com.monkeyteam.chimpagne.model.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.containsTagsFilter
import com.monkeyteam.chimpagne.model.database.happensOnThisDateFilter
import com.monkeyteam.chimpagne.model.database.onlyPublicFilter
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.Location.Companion.convertNameToLocation
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FindEventsViewModel : ViewModel() {
  // UI state exposed to the UI
  private val _uiState = MutableStateFlow(FindEventsUIState())
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
      var filter =
          Filter.and(onlyPublicFilter(), happensOnThisDateFilter(_uiState.value.searchByDate))
      if (_uiState.value.searchByTags.isNotEmpty())
          filter = Filter.and(filter, containsTagsFilter(_uiState.value.searchByTags))

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
              Log.d("FETCHING EVENTS WITHOUT LOCATION QUERY", "Error : ", it)
              onFailure(it)
            },
        )
      }
    }
  }

  fun getListOfFilteredEvents(): List<ChimpagneEvent> {
    return _uiState.value.listOfEvents
  }

  fun getLocationSearchQuery(): String {
    return _uiState.value.searchByLocation
  }

  fun getPossibleLocationList(): List<Location> {
    return _uiState.value.possibleLocationsList
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

  fun getActualLocation(): Location {
    return _uiState.value.actualLocation
  }

  fun updateActualLocation(newLocation: Location, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(actualLocation = newLocation)
    _uiState.value = _uiState.value.copy(searchByLocation = newLocation.toString())
    fetchAllEventsByQueries(onFailure)
  }

  fun getLocationSearchRadius(): Double {
    return _uiState.value.radiusAroundLocationInM
  }

  fun updateLocationSearchRadius(newRadiusInM: Double, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(radiusAroundLocationInM = newRadiusInM)
    fetchAllEventsByQueries(onFailure)
  }

  fun getTagsQuery(): List<String> {
    return _uiState.value.searchByTags
  }

  fun updateTagsQuery(newTagList: List<String>, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByTags = newTagList)
    fetchAllEventsByQueries(onFailure)
  }

  fun getDateQuery(): Calendar {
    return _uiState.value.searchByDate
  }

  fun updateDateQuery(newQuery: Calendar, onFailure: (Exception) -> Unit = {}) {
    _uiState.value = _uiState.value.copy(searchByDate = newQuery)
    fetchAllEventsByQueries(onFailure)
  }
}

data class FindEventsUIState(
    val listOfEvents: List<ChimpagneEvent> = emptyList(),
    val searchByLocation: String = "",
    val actualLocation: Location = Location(),
    val possibleLocationsList: List<Location> = emptyList(),
    val radiusAroundLocationInM: Double = 0.0,
    val searchByTags: List<String> = emptyList(),
    val searchByDate: Calendar = Calendar.getInstance()
)
