package com.monkeyteam.chimpagne

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventManager
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindEventViewModelTests {
  @get:Rule val composeTestRule = createComposeRule()

  private val testEvent1 =
      ChimpagneEvent(
          "0",
          "Party 1",
          "",
          Location("EPFL", 46.519124, 6.567593),
          true,
          listOf("vegan", "concert", "booze"),
          emptyList(),
          emptyMap(),
          buildTimestamp(9, 5, 2024, 5, 0),
          buildTimestamp(10, 5, 2024, 5, 0))

  private val testEvent2 =
      ChimpagneEvent(
          "1",
          "Party 2",
          "",
          Location("EPFL", 46.519130, 6.567580),
          true,
          listOf("vegan", "concert", "family friendly"),
          emptyList(),
          emptyMap(),
          buildTimestamp(8, 5, 2024, 6, 0),
          buildTimestamp(9, 5, 2024, 6, 0))

  private val testEvent3 =
      ChimpagneEvent(
          "2",
          "Party 3",
          "",
          Location("center of earth", 0.0, 0.0),
          true,
          listOf("vegan", "family friendly"),
          emptyList(),
          emptyMap(),
          buildTimestamp(7, 5, 2024, 6, 0),
          buildTimestamp(10, 5, 2024, 6, 0))

  private val eventManager: ChimpagneEventManager =
      ChimpagneEventManager(Firebase.firestore.collection("testevents"))

  @Test
  fun TestFindEventVMSetterFunctions() {
    val findEventVM = FindEventsViewModel(eventManager)
    val location = Location("EPFL", 2.0, 4.0)
    val searchRadius = 6.25
    val tags = listOf("this tag", "that tag", "our tag")
    val date = buildCalendar(4, 2, 4, 0, 0)

    findEventVM.updateSelectedLocation(location)
    assert(findEventVM.uiState.value.selectedLocation!!.name == location.name)
    assert(findEventVM.uiState.value.selectedLocation!!.latitude == location.latitude)
    assert(findEventVM.uiState.value.selectedLocation!!.longitude == location.longitude)
    assert(findEventVM.uiState.value.selectedLocation!!.geohash == location.geohash)

    findEventVM.updateLocationSearchRadius(searchRadius)
    assert(findEventVM.uiState.value.radiusAroundLocationInM == searchRadius)

    findEventVM.updateTags(tags)
    assert(findEventVM.uiState.value.selectedTags.size == tags.size)
    assert(findEventVM.uiState.value.selectedTags.toSet() == tags.toSet())

    findEventVM.updateSelectedDate(date)
    assert(findEventVM.uiState.value.selectedDate == date)
  }

  @Test
  fun TestFindEventVMTestFilter() {
    val eventID1: String
    val eventID2: String
    val eventID3: String

    val eventCreationVM1 = EventViewModel(eventManager = eventManager)

    eventCreationVM1.updateEventTitle(testEvent1.title)
    eventCreationVM1.updateEventDescription(testEvent1.description)
    eventCreationVM1.updateEventLocation(testEvent1.location)
    eventCreationVM1.updateEventPublicity(testEvent1.public)
    eventCreationVM1.updateEventTags(testEvent1.tags)
    eventCreationVM1.updateEventStartCalendarDate(testEvent1.startsAt())
    eventCreationVM1.updateEventEndCalendarDate(testEvent1.endsAt())

    eventCreationVM1.createTheEvent()

    while (eventCreationVM1.uiState.value.loading) {}
    eventID1 = eventCreationVM1.uiState.value.id

    val eventCreationVM2 = EventViewModel(eventManager = eventManager)

    eventCreationVM2.updateEventTitle(testEvent2.title)
    eventCreationVM2.updateEventDescription(testEvent2.description)
    eventCreationVM2.updateEventLocation(testEvent2.location)
    eventCreationVM2.updateEventPublicity(testEvent2.public)
    eventCreationVM2.updateEventTags(testEvent2.tags)
    eventCreationVM2.updateEventStartCalendarDate(testEvent2.startsAt())
    eventCreationVM2.updateEventEndCalendarDate(testEvent2.endsAt())

    eventCreationVM2.createTheEvent()

    while (eventCreationVM2.uiState.value.loading) {}
    eventID2 = eventCreationVM2.uiState.value.id

    val eventCreationVM3 = EventViewModel(eventManager = eventManager)

    eventCreationVM3.updateEventTitle(testEvent3.title)
    eventCreationVM3.updateEventDescription(testEvent3.description)
    eventCreationVM3.updateEventLocation(testEvent3.location)
    eventCreationVM3.updateEventPublicity(testEvent3.public)
    eventCreationVM3.updateEventTags(testEvent3.tags)
    eventCreationVM3.updateEventStartCalendarDate(testEvent3.startsAt())
    eventCreationVM3.updateEventEndCalendarDate(testEvent3.endsAt())

    eventCreationVM3.createTheEvent()

    while (eventCreationVM3.uiState.value.loading) {}
    eventID3 = eventCreationVM3.uiState.value.id

    val eventFinderVM = FindEventsViewModel(eventManager)

    eventFinderVM.updateSelectedLocation(testEvent1.location)
    eventFinderVM.updateLocationSearchRadius(1.0)
    eventFinderVM.updateSelectedDate(testEvent1.startsAt())

    eventFinderVM.fetchEvents({ assertTrue(true) }, { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateLocationSearchRadius(100.0)

    eventFinderVM.fetchEvents({ assertTrue(true) }, { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateTags(listOf("booze"))

    eventFinderVM.fetchEvents({ assertTrue(true) }, { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateSelectedLocation(testEvent3.location)
    eventFinderVM.updateTags(emptyList())

    eventFinderVM.fetchEvents({ assertTrue(true) }, { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventCreationVM1.deleteTheEvent()
    while (eventCreationVM1.uiState.value.loading) {}
    eventCreationVM2.deleteTheEvent()
    while (eventCreationVM2.uiState.value.loading) {}
    eventCreationVM3.deleteTheEvent()
    while (eventCreationVM3.uiState.value.loading) {}
  }
}
