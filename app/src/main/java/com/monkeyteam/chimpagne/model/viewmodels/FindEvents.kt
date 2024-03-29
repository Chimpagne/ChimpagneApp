package com.monkeyteam.chimpagne.model.viewmodels

import android.hardware.camera2.CaptureFailure
import android.util.Log
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.containsInDescription
import com.monkeyteam.chimpagne.model.database.containsInTitle
import com.monkeyteam.chimpagne.model.database.containsTagsFilter
import com.monkeyteam.chimpagne.model.database.startsBeforeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.endsAfterFilter
import com.monkeyteam.chimpagne.model.database.endsBeforeFilter
import com.monkeyteam.chimpagne.model.database.onlyPublic
import com.monkeyteam.chimpagne.model.database.startsAfterFilter
import com.monkeyteam.chimpagne.model.location.Location.Companion.convertNameToLocation

class FindEventsViewModel : ViewModel() {
    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(FindEventsUIState())
    val uiState: StateFlow<FindEventsUIState> = _uiState
    private val fireBaseDB = Database()

    init{
        viewModelScope.launch {
            fireBaseDB.eventManager.getAllEvents(
                {
                    _uiState.value = _uiState.value.copy(listOfEvents = it)
                },
                {
                    Log.d("FETCHING ALL EVENTS", "Error : ", it)
                }
            )
        }
    }
    private fun fetchAllEventsByQueries(onFailure: (Exception) -> Unit = {}) {
        viewModelScope.launch {
            val filter = Filter.and(
                onlyPublic(),
                Filter.or(
                    containsInTitle(_uiState.value.searchByTitleOrDescription),
                    containsInDescription(_uiState.value.searchByTitleOrDescription)
                    ),
                if(_uiState.value.searchByTags.isNotEmpty()) containsTagsFilter(_uiState.value.searchByTags) else Filter(),
                if(_uiState.value.searchByStartsBefore != null) startsBeforeFilter(_uiState.value.searchByStartsBefore!!) else Filter(),
                if(_uiState.value.searchByStartsAfter != null) startsAfterFilter(_uiState.value.searchByStartsAfter!!) else Filter(),
                if(_uiState.value.searchByEndsBefore != null) endsBeforeFilter(_uiState.value.searchByEndsBefore!!) else Filter(),
                if(_uiState.value.searchByEndsAfter != null) endsAfterFilter(_uiState.value.searchByEndsAfter!!) else Filter()
            )

            if(_uiState.value.possibleLocationsList.isNotEmpty()){
                fireBaseDB.eventManager.getAllEventsByFilterAroundLocation(
                    _uiState.value.possibleLocationsList[0],
                    _uiState.value.radiusAroundLocationInM,
                    {_uiState.value = _uiState.value.copy(listOfEvents = it)},
                    {
                        Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
                        onFailure(it)
                    },
                    filter
                )
            }else{
                fireBaseDB.eventManager.getAllEventsByFilter(
                    filter,
                    {_uiState.value = _uiState.value.copy(listOfEvents = it)},
                    {
                        Log.d("FETCHING EVENTS BY LOCATION QUERY", "Error : ", it)
                        onFailure(it)
                    },
                )
            }
        }
    }
    fun updateTitleOrDescriptionQuery(newQuery: String, onFailure: (Exception) -> Unit = {}) {
        _uiState.value = _uiState.value.copy(searchByTitleOrDescription = newQuery)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateLocationQuery(newQuery: String, onFailure: (Exception) -> Unit = {}) {
        _uiState.value = _uiState.value.copy(searchByLocation = newQuery)
        convertNameToLocation(
            _uiState.value.searchByLocation,
            {
                _uiState.value = _uiState.value.copy(possibleLocationsList = it)
                fetchAllEventsByQueries(onFailure)
            }
        )
    }

    fun updateLocationSearchRadius(newRadiusInM: Double, onFailure: (Exception) -> Unit = {}) {
        _uiState.value = _uiState.value.copy(radiusAroundLocationInM = newRadiusInM)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateTagsQuery(newTagList: List<String>, onFailure: (Exception) -> Unit = {}){
        _uiState.value = _uiState.value.copy(searchByTags = newTagList)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateStartsBeforeQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}){
        _uiState.value = _uiState.value.copy(searchByStartsBefore = newQuery)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateStartsAfterQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}){
        _uiState.value = _uiState.value.copy(searchByStartsAfter = newQuery)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateEndsBeforeQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}){
        _uiState.value = _uiState.value.copy(searchByEndsBefore = newQuery)
        fetchAllEventsByQueries(onFailure)
    }

    fun updateEndsAfterQuery(newQuery: Timestamp?, onFailure: (Exception) -> Unit = {}){
        _uiState.value = _uiState.value.copy(searchByEndsAfter = newQuery)
        fetchAllEventsByQueries(onFailure)
    }
}

data class FindEventsUIState(
    val listOfEvents: List<ChimpagneEvent> = emptyList(),
    val searchByTitleOrDescription: String = "",
    val searchByLocation: String = "",
    val possibleLocationsList: List<Location> = emptyList(),
    val radiusAroundLocationInM: Double = 0.0,
    val searchByTags : List<String> = emptyList(),
    val searchByStartsBefore : Timestamp? = null,
    val searchByStartsAfter : Timestamp? = null,
    val searchByEndsBefore : Timestamp? = null,
    val searchByEndsAfter : Timestamp? = null,
)