package com.monkeyteam.chimpagne.newtests.viewmodels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import junit.framework.TestCase.assertEquals
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

  @Test
  fun updateSupplyTests() {
    val account = TEST_ACCOUNTS[0]
    var event = TEST_EVENTS[0]
    var supply = ChimpagneSupply(id = "1", quantity = 1, unit = "banana", description = "hey")

    database.accountManager.signInTo(account)
    initializeTestDatabase()

    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    val eventViewModel = EventViewModel(event.id, database)
    while (eventViewModel.uiState.value.loading) {}

    event = event.copy(supplies = event.supplies - "1")
    eventViewModel.removeSupplyAtomically("1")
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(event.supplies, eventViewModel.uiState.value.supplies)

    event = event.copy(supplies = event.supplies + (supply.id to supply))
    eventViewModel.updateSupplyAtomically(supply)
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(event.supplies, eventViewModel.uiState.value.supplies)

    val newSupplies =
        event.supplies +
            (supply.id to
                supply.copy(assignedTo = supply.assignedTo + (account.firebaseAuthUID to true)))
    event = event.copy(supplies = newSupplies)
    eventViewModel.assignSupplyAtomically(supply.id, account.firebaseAuthUID)
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(event.supplies, eventViewModel.uiState.value.supplies)

    val newNewSupplies =
        event.supplies +
            (supply.id to supply.copy(assignedTo = supply.assignedTo - account.firebaseAuthUID))
    event = event.copy(supplies = newNewSupplies)
    eventViewModel.unassignSupplyAtomically(supply.id, account.firebaseAuthUID)
    while (eventViewModel.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(event.supplies, eventViewModel.uiState.value.supplies)
  }

  @Test
  fun testCreateEventSuccess() {
    val eventVM = EventViewModel(database = database)
    replaceVMEventBy(eventVM, testEvent)

    eventVM.createTheEvent(
        onSuccess = { assertTrue(true) },
        onInvalidInputs = { assertTrue(false) },
        onFailure = { assertTrue(false) })

    while (eventVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventVM.uiState.value.id.isNotEmpty())
  }

  @Test
  fun testCreateEventWithEmptyTitle() {
    val eventVM = EventViewModel(database = database)
    replaceVMEventBy(eventVM, testEvent.copy(title = ""))

    eventVM.createTheEvent(
        onSuccess = { assertTrue(false) },
        onInvalidInputs = { assertEquals("Title should not be empty", it) },
        onFailure = { assertTrue(false) })

    while (eventVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventVM.uiState.value.id.isEmpty())
  }

  private fun replaceVMEventBy2(eventVM: EventViewModel, event: ChimpagneEvent) {
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

  @Test
  fun testCreateEventWithEqualDates() {
    val invalidEvent =
        ChimpagneEvent(
            id = "FIRST_EVENT",
            title = "First event",
            description = "a random description",
            location = Location("EPFL", 46.519124, 6.567593),
            public = true,
            tags = listOf("vegan", "monkeys"),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(9, 5, 2024, 15, 15),
            endsAtTimestamp = buildTimestamp(9, 5, 2024, 15, 15),
            ownerId = "JUAN",
            supplies =
                mapOf(
                    "1" to ChimpagneSupply("1", "d", 1, "g"),
                    "2" to ChimpagneSupply("2", "ff", 2, "d"),
                    "3" to ChimpagneSupply("3", "ee", 3, "e")),
            parkingSpaces = 1,
            beds = 2)

    val eventVM = EventViewModel(database = database)
    replaceVMEventBy(eventVM, invalidEvent)

    eventVM.createTheEvent(
        onSuccess = { assertTrue(false) },
        onInvalidInputs = { assertEquals("Invalid dates", it) },
        onFailure = { assertTrue(false) })

    while (eventVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventVM.uiState.value.id.isEmpty())
  }
  @Test
  fun pollFunctionalityTest() {
    initializeTestDatabase()

    val eventSearchVM =
        EventViewModel(
            eventID = testEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    // Wait for database to get the data
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(testEvent.id, eventSearchVM.uiState.value.id)

    val poll =
        ChimpagnePoll(
            title = "monkey",
            query = "are you a monkey ?",
            options = listOf("yes", "no", "I wish not to answer"),
            votes = emptyMap())

    eventSearchVM.createPollAtomically(
        poll = poll, onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM.uiState.value.polls.keys.contains(poll.id))

    val eventSearchVM2 =
        EventViewModel(
            eventID = testEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventSearchVM2.uiState.value.polls.keys.contains(poll.id))

    database.accountManager.signInTo(TEST_ACCOUNTS[0])
    eventSearchVM2.castPollVoteAtomically(
        pollId = poll.id,
        optionIndex = 0,
        onSuccess = { assertTrue(true) },
        onFailure = { assertTrue(false) })
    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[1])
    eventSearchVM2.castPollVoteAtomically(
        pollId = poll.id,
        optionIndex = 0,
        onSuccess = { assertTrue(true) },
        onFailure = { assertTrue(false) })
    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    database.accountManager.signInTo(TEST_ACCOUNTS[2])
    eventSearchVM2.castPollVoteAtomically(
        pollId = poll.id,
        optionIndex = 1,
        onSuccess = { assertTrue(true) },
        onFailure = { assertTrue(false) })

    while (eventSearchVM2.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(
        eventSearchVM2.uiState.value.polls[poll.id]!!.votes.keys.toSet(),
        setOf(
            TEST_ACCOUNTS[0].firebaseAuthUID,
            TEST_ACCOUNTS[1].firebaseAuthUID,
            TEST_ACCOUNTS[2].firebaseAuthUID))

    assertEquals(
        0, eventSearchVM2.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[0].firebaseAuthUID])
    assertEquals(
        0, eventSearchVM2.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[1].firebaseAuthUID])
    assertEquals(
        1, eventSearchVM2.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[2].firebaseAuthUID])

    assertEquals(2, eventSearchVM2.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[0])
    assertEquals(1, eventSearchVM2.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[1])
    assertEquals(0, eventSearchVM2.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[2])

    val eventSearchVM3 =
        EventViewModel(
            eventID = testEvent.id,
            database = database,
            onSuccess = { assertTrue(true) },
            onFailure = { assertTrue(false) })

    while (eventSearchVM3.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(
        eventSearchVM3.uiState.value.polls[poll.id]!!.votes.keys.toSet(),
        setOf(
            TEST_ACCOUNTS[0].firebaseAuthUID,
            TEST_ACCOUNTS[1].firebaseAuthUID,
            TEST_ACCOUNTS[2].firebaseAuthUID))

    assertEquals(
        0, eventSearchVM3.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[0].firebaseAuthUID])
    assertEquals(
        0, eventSearchVM3.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[1].firebaseAuthUID])
    assertEquals(
        1, eventSearchVM3.uiState.value.polls[poll.id]!!.votes[TEST_ACCOUNTS[2].firebaseAuthUID])

    assertEquals(2, eventSearchVM3.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[0])
    assertEquals(1, eventSearchVM3.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[1])
    assertEquals(0, eventSearchVM3.uiState.value.polls[poll.id]!!.getNumberOfVotesPerOption()[2])

    eventSearchVM3.deletePollAtomically(
        pollId = poll.id, onSuccess = { assertTrue(true) }, onFailure = { assertTrue(false) })
    while (eventSearchVM3.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(false, eventSearchVM3.uiState.value.polls.containsKey(poll.id))

    database.accountManager.signInTo(TEST_ACCOUNTS[1])
  }
}
