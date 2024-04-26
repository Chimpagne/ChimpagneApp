package com.monkeyteam.chimpagne.newtests.viewmodels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventViewModelTests {

  val database = Database()
  private val eventManager = database.eventManager

  @Before
  fun signIn() {
    database.accountManager.signInTo(ChimpagneAccount())
  }

  @get:Rule val composeTestRule = createComposeRule()

  private val testEvent =
      ChimpagneEvent(
          "0",
          "SWENT",
          "swent party",
          Location("EPFL", 46.518659400000004, 6.566561505148001),
          true,
          listOf("vegan", "wild"),
          emptyMap(),
          emptyMap(),
          buildTimestamp(9, 5, 2024, 0, 0),
          buildTimestamp(10, 5, 2024, 0, 0))

  @Test
  fun TestVMSetterFunctions() {

    val eventVM = EventViewModel(database = database)

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

    eventVM.updateEventStartCalendarDate(testEvent.startsAt())
    assertTrue(eventVM.uiState.value.startsAtCalendarDate.time == testEvent.startsAt().time)

    eventVM.updateEventEndCalendarDate(testEvent.endsAt())
    assertTrue(eventVM.uiState.value.endsAtCalendarDate.time == testEvent.endsAt().time)
  }

  @Test
  fun TestCreateSearchDeleteAnEvent() {
    /* TODO JUAN FIX THIS LATER

    val eventCreationVM = EventViewModel(database = database)

    eventCreationVM.updateEventTitle(testEvent.title)
    eventCreationVM.updateEventDescription(testEvent.description)
    eventCreationVM.updateEventLocation(testEvent.location)
    eventCreationVM.updateEventPublicity(testEvent.public)
    eventCreationVM.updateEventTags(testEvent.tags)
    eventCreationVM.updateEventStartCalendarDate(testEvent.startsAt())
    eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt())

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            database = database,
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
    assertTrue(eventSearchVM.uiState.value.startsAtCalendarDate.time == testEvent.startsAt().time)
    assertTrue(eventSearchVM.uiState.value.endsAtCalendarDate.time == testEvent.endsAt().time)

    eventSearchVM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.id == "")

     */
  }

  @Test
  fun TestUpdateAnEvent() {

    /* TODO JUAN FIX THIS LATER
    val testUpdatedEvent =
        ChimpagneEvent(
            "",
            "London",
            "Harry Potter",
            Location("United Kingdown", 38.8534951, 12.3483915),
            false,
            listOf("magic", "wands"),
            emptyMap(),
            emptyMap(),
            buildTimestamp(4, 1, 2025, 2, 3),
            buildTimestamp(5, 1, 2025, 2, 3))

    val eventCreationVM = EventViewModel(database = database)

    eventCreationVM.updateEventTitle(testEvent.title)
    eventCreationVM.updateEventDescription(testEvent.description)
    eventCreationVM.updateEventLocation(testEvent.location)
    eventCreationVM.updateEventPublicity(testEvent.public)
    eventCreationVM.updateEventTags(testEvent.tags)
    eventCreationVM.updateEventStartCalendarDate(testEvent.startsAt())
    eventCreationVM.updateEventEndCalendarDate(testEvent.endsAt())

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            database = database,
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
    eventSearchVM.updateEventStartCalendarDate(testUpdatedEvent.startsAt())
    eventSearchVM.updateEventEndCalendarDate(testUpdatedEvent.endsAt())

    eventSearchVM.updateTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    val eventSearch2VM =
        EventViewModel(
            eventID = eventID,
            database = database,
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
    assertTrue(
        eventSearch2VM.uiState.value.startsAtCalendarDate.time == testUpdatedEvent.startsAt().time)
    assertTrue(
        eventSearch2VM.uiState.value.endsAtCalendarDate.time == testUpdatedEvent.endsAt().time)

    eventSearch2VM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

     */
  }
}
