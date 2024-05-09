package com.monkeyteam.chimpagne.newtests.viewmodels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
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
    database.accountManager.signInTo(TEST_ACCOUNTS[1])
  }

  @get:Rule val composeTestRule = createComposeRule()

  private val testEvent = TEST_EVENTS[0]
  private val testUpdateEvent = TEST_EVENTS[1]
  private val testStaffedEvent = TEST_EVENTS[2]

  private fun replaceVMEventBy(eventVM: EventViewModel, event: ChimpagneEvent) {
    eventVM.updateEventTitle(event.title)
    eventVM.updateEventDescription(event.description)
    eventVM.updateEventLocation(event.location)
    eventVM.updateEventPublicity(event.public)
    eventVM.updateEventTags(event.tags)
    eventVM.updateEventStartCalendarDate(event.startsAt())
    eventVM.updateEventEndCalendarDate(event.endsAt())
    eventVM.updateEventSupplies(event.supplies)
    eventVM.updateParkingSpaces(event.parkingSpaces)
    eventVM.updateBeds(event.beds)
  }

  // DOESN'T COMPARE GUEST OR STAFF LIST BECAUSE IT IS UPDATED DIFFERENTLY//
  private fun assertEqualEventVMWithEvent(eventVM: EventViewModel, event: ChimpagneEvent) {
    assertTrue(eventVM.uiState.value.title == event.title)

    assertTrue(eventVM.uiState.value.location.name == event.location.name)
    assertTrue(eventVM.uiState.value.location.latitude == event.location.latitude)
    assertTrue(eventVM.uiState.value.location.longitude == event.location.longitude)
    assertTrue(eventVM.uiState.value.location.geohash == event.location.geohash)

    assertTrue(eventVM.uiState.value.public == event.public)

    assertTrue(eventVM.uiState.value.tags.size == event.tags.size)
    assertTrue(eventVM.uiState.value.tags.toSet() == event.tags.toSet())

    assertTrue(eventVM.uiState.value.startsAtCalendarDate.time == event.startsAt().time)
    assertTrue(eventVM.uiState.value.endsAtCalendarDate.time == event.endsAt().time)

    assertTrue(eventVM.uiState.value.supplies.size == event.supplies.size)
    assertTrue(eventVM.uiState.value.supplies.values.toSet() == event.supplies.values.toSet())

    assertTrue(eventVM.uiState.value.parkingSpaces == event.parkingSpaces)
    assertTrue(eventVM.uiState.value.beds == event.beds)
  }

  @Test
  fun TestVMSetterFunctions() {

    val eventVM = EventViewModel(database = database)

    replaceVMEventBy(eventVM, testEvent)
    assertEqualEventVMWithEvent(eventVM, testEvent)
  }

  @Test
  fun TestCreateSearchDeleteAnEvent() {

    val eventCreationVM = EventViewModel(database = database)

    replaceVMEventBy(eventCreationVM, testEvent)
    assertEqualEventVMWithEvent(eventCreationVM, testEvent)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEqualEventVMWithEvent(eventSearchVM, testEvent)

    eventSearchVM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.id == "")
  }

  @Test
  fun TestUpdateAnEvent() {
    val eventCreationVM = EventViewModel(database = database)

    replaceVMEventBy(eventCreationVM, testEvent)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.id == eventID)

    replaceVMEventBy(eventSearchVM, testUpdateEvent)
    assertEqualEventVMWithEvent(eventSearchVM, testUpdateEvent)

    eventSearchVM.updateTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    val eventSearch2VM =
        EventViewModel(
            eventID = eventID,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearch2VM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEqualEventVMWithEvent(eventSearch2VM, testUpdateEvent)

    eventSearch2VM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearch2VM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)
  }

  @Test
  fun TestAddRemoveGuestFromEvent() {
    val eventCreationVM = EventViewModel(database = database)

    replaceVMEventBy(eventCreationVM, testEvent)

    eventCreationVM.createTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventCreationVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    val eventID = eventCreationVM.uiState.value.id

    val eventSearchVM =
        EventViewModel(
            eventID = eventID,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.guests.isEmpty())

    database.accountManager.signInTo(TEST_ACCOUNTS[0])
    eventSearchVM.joinTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[1])
    eventSearchVM.joinTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[2])
    eventSearchVM.joinTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.guests.size == 3)
    assertTrue(
        eventSearchVM.uiState.value.guests.keys.toSet() ==
            setOf(
                TEST_ACCOUNTS[0].firebaseAuthUID,
                TEST_ACCOUNTS[1].firebaseAuthUID,
                TEST_ACCOUNTS[2].firebaseAuthUID,
            ))

    eventSearchVM.leaveTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[1])
    eventSearchVM.leaveTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[0])
    eventSearchVM.leaveTheEvent(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.guests.isEmpty())

    eventSearchVM.deleteTheEvent(
        onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)
  }

  @Test
  fun TestStaffFunctionnality() {
    initializeTestDatabase()
    database.accountManager.signInTo(TEST_ACCOUNTS[1])

    val eventSearchVM =
        EventViewModel(
            eventID = testStaffedEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.id == testStaffedEvent.id)

    eventSearchVM.fetchAccounts(onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.accounts.keys.size == 2)
    assertTrue(eventSearchVM.uiState.value.accounts[TEST_ACCOUNTS[0].firebaseAuthUID] != null)
    assertTrue(eventSearchVM.uiState.value.accounts[TEST_ACCOUNTS[1].firebaseAuthUID] != null)

    assertTrue(eventSearchVM.uiState.value.guests.size == 1)
    assertTrue(eventSearchVM.uiState.value.staffs.isEmpty())

    eventSearchVM.promoteGuestToStaff(TEST_ACCOUNTS[0].firebaseAuthUID)
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.staffs.size == 1)
    assertTrue(eventSearchVM.uiState.value.guests.isEmpty())

    val eventSearchVM2 =
        EventViewModel(
            eventID = testStaffedEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM2.uiState.value.staffs.size == 1)
    assertTrue(eventSearchVM2.uiState.value.guests.isEmpty())

    eventSearchVM2.demoteStaffToGuest(TEST_ACCOUNTS[0].firebaseAuthUID)
    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM2.uiState.value.guests.size == 1)
    assertTrue(eventSearchVM2.uiState.value.staffs.isEmpty())

    val eventSearchVM3 =
        EventViewModel(
            eventID = testStaffedEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    while (eventSearchVM3.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM3.uiState.value.guests.size == 1)
    assertTrue(eventSearchVM3.uiState.value.staffs.isEmpty())
  }
}
