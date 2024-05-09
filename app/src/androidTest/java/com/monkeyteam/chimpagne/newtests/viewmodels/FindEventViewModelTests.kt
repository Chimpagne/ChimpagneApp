package com.monkeyteam.chimpagne.newtests.viewmodels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindEventViewModelTests {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun initTest() {
    initializeTestDatabase()
    database.accountManager.signInTo(ChimpagneAccount())
  }

  @Test
  fun TestFindEventVMSetterFunctions() {
    val findEventVM = FindEventsViewModel(database = database)
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

    findEventVM.setLoading(true)
    assert(findEventVM.uiState.value.loading)

    findEventVM.setLoading(false)
    assert(!findEventVM.uiState.value.loading)
  }

  @Test
  fun TestFindEventSystem() {
    val testEvent1 = TEST_EVENTS[0]
    val testEvent2 = TEST_EVENTS[1]
    val testEvent3 = TEST_EVENTS[2]

    val eventID1 = testEvent1.id
    val eventID2 = testEvent2.id
    val eventID3 = testEvent3.id

    val eventFinderVM = FindEventsViewModel(database = database)

    eventFinderVM.updateSelectedLocation(testEvent1.location)
    eventFinderVM.updateLocationSearchRadius(1.0)
    eventFinderVM.updateSelectedDate(testEvent1.endsAt())

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateLocationSearchRadius(100.0)

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateTags(listOf("vegan"))

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateTags(listOf("monkeys"))

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateSelectedDate(testEvent1.startsAt())

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateSelectedLocation(testEvent3.location)
    eventFinderVM.updateTags(emptyList())

    eventFinderVM.fetchEvents(
        {
          eventFinderVM.setLoading(false)
          assertTrue(true)
        },
        { assertTrue(false) })

    while (eventFinderVM.uiState.value.loading) {}

    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID1))
    assertFalse(eventFinderVM.uiState.value.events.containsKey(eventID2))
    assertTrue(eventFinderVM.uiState.value.events.containsKey(eventID3))

    eventFinderVM.updateTags(listOf("juan"))

    eventFinderVM.fetchEvents(
        { assertTrue(false) },
        {
          if (it.message == "No events found") {
            eventFinderVM.setLoading(false)
            assertTrue(true)
          } else {
            assertTrue(false)
          }
        })
  }

  @Test
  fun testFetchEvent() {
    val eventId = "houhou"
    val mockEvent = ChimpagneEvent(id = eventId)

    val findEventVM = FindEventsViewModel(database = database)

    findEventVM.fetchEvent(
        id = eventId,
        onSuccess = {
          assertTrue(
              "Event should be loaded and not null",
              findEventVM.uiState.value.events[eventId] != null)
          assertEquals(
              "Check event details", mockEvent.id, findEventVM.uiState.value.events[eventId]?.id)
        },
        onFailure = { fail("Expected success but got failure") })

    // Test the scenario where the event does not exist
    val nonExistentEventId = "nonexistent"
    findEventVM.fetchEvent(
        id = nonExistentEventId,
        onSuccess = { fail("Expected failure but got success for nonexistent event") },
        onFailure = { assertTrue("Should handle the failure due to nonexistent event", true) })

    assertNull(
        "Nonexistent event should not be added",
        findEventVM.uiState.value.events[nonExistentEventId])
  }

  @Test
  fun TestJoinAnEvent() {
    val testAccount = TEST_ACCOUNTS[2]
    val testEvent = TEST_EVENTS[0]

    val accountViewModel = AccountViewModel(database = database)
    accountViewModel.loginToChimpagneAccount(testAccount.firebaseAuthUID, {}, {})
    while (accountViewModel.uiState.value.loading) {}

    val findEventVM = FindEventsViewModel(database = database)
    findEventVM.joinEvent(testEvent.id, { assertTrue(true) }, { assertTrue(false) })
    while (findEventVM.uiState.value.loading) {}

    val eventSearchVM =
        EventViewModel(
            eventID = testEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}

    assertTrue(eventSearchVM.uiState.value.guests[testAccount.firebaseAuthUID] == true)

    eventSearchVM.leaveTheEvent()
    while (eventSearchVM.uiState.value.loading) {}
  }
}
