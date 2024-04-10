package com.monkeyteam.chimpagne

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventManager
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.viewmodels.EventViewModel
import java.util.Calendar
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventViewModelTests {
  @get:Rule val composeTestRule = createComposeRule()

  private val eventManager: ChimpagneEventManager =
      ChimpagneEventManager(Firebase.firestore.collection("testevents"))

  @Test
  fun TestVMSetterFunctions() {
    val startCalendarDate = Calendar.getInstance()
    startCalendarDate.set(2024, 5, 9, 0, 0, 0)

    val endCalendarDate = Calendar.getInstance()
    endCalendarDate.set(2024, 5, 10, 0, 0, 0)

    val testEvent =
        ChimpagneEvent(
            "0",
            "SWENT",
            "swent party",
            Location("EPFL", 46.518659400000004, 6.566561505148001),
            true,
            listOf("vegan", "wild"),
            emptyMap(),
            Timestamp(startCalendarDate.time),
            Timestamp(endCalendarDate.time))

    val eventVM = EventViewModel(eventManager = eventManager)

    eventVM.updateEventTitle(testEvent.title)
    assertTrue(eventVM.uiState.value.title == testEvent.title)

    eventVM.updateEventDescription(testEvent.description)
    assertTrue(eventVM.uiState.value.description == testEvent.description)

    eventVM.updateEventLocation(testEvent.location)
    assertTrue(eventVM.uiState.value.location.name == testEvent.location.name)
    assertTrue(eventVM.uiState.value.location.latitude == testEvent.location.latitude)
    assertTrue(eventVM.uiState.value.location.longitude == testEvent.location.longitude)
    assertTrue(eventVM.uiState.value.location.geohash == testEvent.location.geohash)

    eventVM.updateEventPublicity(testEvent.public)
    assertTrue(eventVM.uiState.value.public == testEvent.public)

    eventVM.updateEventTags(testEvent.tags)
    assertTrue(eventVM.uiState.value.tags.size == testEvent.tags.size)
    assertTrue(eventVM.uiState.value.tags.toSet() == testEvent.tags.toSet())

    eventVM.updateEventStartCalendarDate(testEvent.startAt)
    assertTrue(eventVM.uiState.value.startsAtCalendarDate == testEvent.startAt)

    eventVM.updateEventEndCalendarDate(testEvent.endsAt)
    assertTrue(eventVM.uiState.value.endsAtCalendarDate == testEvent.endsAt)
  }

  /*@Test
  fun TestCalendar() {
      val startCalendarDate = buildCalendar(9, 5, 2024, 0, 0)
      val startTimestamp = buildTimestamp(startCalendarDate)
      val testEvent = ChimpagneEvent(startsAtTimestamp = startTimestamp)

      val eventCreationVM = EventViewModel(eventManager = eventManager)

      eventCreationVM.updateEventStartCalendarDate(testEvent.startAt)
      eventCreationVM.createTheEvent()

      while (eventCreationVM.uiState.value.loading){}

      val eventID = eventCreationVM.uiState.value.id

      val eventSearchVM =
          EventViewModel(eventID = eventID, eventManager = eventManager)

      while (eventSearchVM.uiState.value.loading){}

      Log.d("EVENTS: CALENDAR", startCalendarDate.time.toString())
      Log.d("EVENTS: TIMESTAMP", startTimestamp.toDate().toString())
      Log.d("EVENTS: VM ORIGINAL", eventCreationVM.uiState.value.startsAtCalendarDate.time.toString())
      Log.d("EVENTS: VM AFTER SEARCH", eventSearchVM.uiState.value.startsAtCalendarDate.time.toString())
  }*/

  @Test
  fun TestCreateSearchDeleteAnEvent() {
    val startCalendarDate = buildCalendar(9, 5, 2024, 0, 0)
    val endCalendarDate = buildCalendar(10, 5, 2024, 0, 0)

    val testEvent =
        ChimpagneEvent(
            "",
            "SWENT",
            "swent party",
            Location("EPFL", 46.518659400000004, 6.566561505148001),
            true,
            listOf("vegan", "wild"),
            emptyMap(),
            buildTimestamp(startCalendarDate),
            buildTimestamp(endCalendarDate))

    val eventCreationVM = EventViewModel(eventManager = eventManager)

    eventCreationVM.updateEventTitle(testEvent.title)
    eventCreationVM.updateEventDescription(testEvent.description)
    eventCreationVM.updateEventLocation(testEvent.location)
    eventCreationVM.updateEventPublicity(testEvent.public)
    eventCreationVM.updateEventTags(testEvent.tags)
    eventCreationVM.updateEventStartCalendarDate(testEvent.startAt)
    eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            eventManager = eventManager,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.title == testEvent.title)
    assertTrue(eventSearchVM.uiState.value.description == testEvent.description)
    assertTrue(eventSearchVM.uiState.value.location.name == testEvent.location.name)
    assertTrue(eventSearchVM.uiState.value.location.latitude == testEvent.location.latitude)
    assertTrue(eventSearchVM.uiState.value.location.longitude == testEvent.location.longitude)
    assertTrue(eventSearchVM.uiState.value.location.geohash == testEvent.location.geohash)
    assertTrue(eventSearchVM.uiState.value.public == testEvent.public)
    assertTrue(eventSearchVM.uiState.value.tags.size == testEvent.tags.size)
    assertTrue(eventSearchVM.uiState.value.tags.toSet() == testEvent.tags.toSet())
    // assertTrue(eventSearchVM.uiState.value.startsAtCalendarDate.time ==
    // testEvent.startAt.time)//TODO
    // assertTrue(eventSearchVM.uiState.value.endsAtCalendarDate == testEvent.endsAt)//TODO

    eventSearchVM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.id == "")
  }

  @Test
  fun TestUpdateAnEvent() {
    val startCalendarDate = buildCalendar(28, 3, 2024, 6, 13)

    val endCalendarDate = buildCalendar(28, 3, 2024, 6, 13)

    val testEvent =
        ChimpagneEvent(
            "",
            "",
            "Default Description",
            Location("Paris", 48.8534951, 2.3483915),
            true,
            emptyList(),
            emptyMap(),
            buildTimestamp(startCalendarDate),
            buildTimestamp(endCalendarDate))

    val testUpdatedEvent =
        ChimpagneEvent(
            "",
            "London",
            "Harry Potter",
            Location("United Kingdown", 38.8534951, 12.3483915),
            false,
            listOf("magic", "wands"),
            emptyMap(),
            buildTimestamp(startCalendarDate),
            buildTimestamp(endCalendarDate))

    val eventCreationVM = EventViewModel(eventManager = eventManager)

    eventCreationVM.updateEventTitle(testEvent.title)
    eventCreationVM.updateEventDescription(testEvent.description)
    eventCreationVM.updateEventLocation(testEvent.location)
    eventCreationVM.updateEventPublicity(testEvent.public)
    eventCreationVM.updateEventTags(testEvent.tags)
    eventCreationVM.updateEventStartCalendarDate(testEvent.startAt)
    eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            eventManager = eventManager,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.id == eventID)

    eventSearchVM.updateEventTitle(testUpdatedEvent.title)
    eventSearchVM.updateEventDescription(testUpdatedEvent.description)
    eventSearchVM.updateEventLocation(testUpdatedEvent.location)
    eventSearchVM.updateEventPublicity(testUpdatedEvent.public)
    eventSearchVM.updateEventTags(testUpdatedEvent.tags)
    eventSearchVM.updateEventStartCalendarDate(testUpdatedEvent.startAt)
    eventSearchVM.updateEventEndCalendarDate(testUpdatedEvent.endsAt)

    eventSearchVM.updateTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    val eventSearch2VM =
        EventViewModel(
            eventID = eventID,
            eventManager = eventManager,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearch2VM.uiState.value.loading) {}

    assertTrue(eventSearch2VM.uiState.value.title == testUpdatedEvent.title)
    assertTrue(eventSearch2VM.uiState.value.description == testUpdatedEvent.description)
    assertTrue(eventSearch2VM.uiState.value.location.name == testUpdatedEvent.location.name)
    assertTrue(eventSearch2VM.uiState.value.location.latitude == testUpdatedEvent.location.latitude)
    assertTrue(
        eventSearch2VM.uiState.value.location.longitude == testUpdatedEvent.location.longitude)
    assertTrue(eventSearch2VM.uiState.value.location.geohash == testUpdatedEvent.location.geohash)
    assertTrue(eventSearch2VM.uiState.value.public == testUpdatedEvent.public)
    assertTrue(eventSearch2VM.uiState.value.tags.size == testUpdatedEvent.tags.size)
    assertTrue(eventSearch2VM.uiState.value.tags.toSet() == testUpdatedEvent.tags.toSet())
    // assertTrue(eventSearch2VM.uiState.value.startsAtCalendarDate.time ==
    // testEvent.startAt.time)//TODO
    // assertTrue(eventSearch2VM.uiState.value.endsAtCalendarDate.time ==
    // testEvent.endsAt.time)//TODO

    eventSearch2VM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
  }

  @Test
  fun TestAddAndRemoveGuestsFromAnEvent() {
    val eventCreationVM = EventViewModel(eventManager = eventManager)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            eventManager = eventManager,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.id == eventID)

    eventSearchVM.addGuestToTheEvent(
        "Clement", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    eventSearchVM.addGuestToTheEvent(
        "Lea", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    eventSearchVM.addGuestToTheEvent(
        "Arnaud", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}

    val guestSet = setOf("Clement", "Lea", "Arnaud")

    assertTrue(eventSearchVM.uiState.value.guests.keys.size == guestSet.size)
    assertTrue(eventSearchVM.uiState.value.guests.keys == guestSet)

    eventSearchVM.removeGuestFromTheEvent(
        "Clement", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    eventSearchVM.removeGuestFromTheEvent(
        "Lea", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    eventSearchVM.removeGuestFromTheEvent(
        "Arnaud", onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.guests.isEmpty())

    eventSearchVM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
  }
}
