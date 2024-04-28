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
      /* Juan fixes this in this PR: https://github.com/Chimpagne/ChimpagneApp/pull/112*/
  }

  @Test
  fun TestUpdateAnEvent() {
      /* Juan fixes this in this PR: https://github.com/Chimpagne/ChimpagneApp/pull/112*/
  }

}
