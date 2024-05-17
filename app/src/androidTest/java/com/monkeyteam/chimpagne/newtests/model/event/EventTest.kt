package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventTest {

  val database = Database()
  val accountManager = database.accountManager
  val eventManager = database.eventManager

  //  val ownerOfEvent0 = TEST_ACCOUNTS[1]
  val event = TEST_EVENTS[0]
  val account0 = TEST_ACCOUNTS[0]
  val account1 = TEST_ACCOUNTS[1]
  val account2 = TEST_ACCOUNTS[2]

  @Before
  fun init() {
    initializeTestDatabase()
    accountManager.signInTo((account0))
  }

  @Test
  fun guestListAndStaffListTest() {
    val event =
        ChimpagneEvent(
            staffs = hashMapOf("1" to true, "2" to true), guests = hashMapOf("3" to true))
    assertEquals(event.guests.keys, event.guestList())
    assertEquals(event.staffs.keys, event.staffList())
  }

  @Test
  fun supplyTest() {
    val supply =
        ChimpagneSupply(
            description = "bananas from la Migros",
            quantity = 4,
            unit = "bananas",
            assignedTo =
                hashMapOf(account0.firebaseAuthUID to true, account1.firebaseAuthUID to true))

    var loading = true
    eventManager.atomic.updateSupply(event.id, supply, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(100)
    var updatedEvent = ChimpagneEvent()
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}

    assertEquals(supply, updatedEvent.supplies[supply.id])

    loading = true
    eventManager.atomic.removeSupply(
        event.id, supply.id, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(100)
    updatedEvent = ChimpagneEvent()
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}
    Thread.sleep(100)
    assertEquals(null, updatedEvent.supplies[supply.id])

    loading = true
    eventManager.atomic.updateSupply(event.id, supply, { loading = false }, { assertTrue(false) })
    while (loading) {}
    updatedEvent = ChimpagneEvent()
    loading = true
    eventManager.atomic.assignSupply(
        event.id, supply.id, account2.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(100)
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}

    assertTrue(updatedEvent.supplies[supply.id]!!.assignedTo[account2.firebaseAuthUID] == true)

    loading = true
    eventManager.atomic.unassignSupply(
        event.id, supply.id, account2.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(100)
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}

    assertNull(updatedEvent.supplies[supply.id]!!.assignedTo[account2.firebaseAuthUID])
  }

  @Test
  fun pollsTest() {
    val poll =
        ChimpagnePoll(
            title = "title",
            query = "is this app for event organisation ?",
            options = listOf("yes", "no"),
            votes = emptyMap())

    var loading = true
    eventManager.atomic.createPoll(event.id, poll, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)
    var updatedEvent = ChimpagneEvent()
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(poll.id, updatedEvent.polls[poll.id]!!.id)

    loading = true
    eventManager.atomic.castPollVote(
        event.id, poll.id, account0.firebaseAuthUID, 0, { loading = false }, { assertTrue(false) })
    while (loading) {}

    loading = true
    eventManager.atomic.castPollVote(
        event.id, poll.id, account1.firebaseAuthUID, 0, { loading = false }, { assertTrue(false) })
    while (loading) {}

    loading = true
    eventManager.atomic.castPollVote(
        event.id, poll.id, account2.firebaseAuthUID, 1, { loading = false }, { assertTrue(false) })
    while (loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    updatedEvent = ChimpagneEvent()
    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(
        updatedEvent.polls[poll.id]!!.votes.keys.toSet(),
        setOf(account0.firebaseAuthUID, account1.firebaseAuthUID, account2.firebaseAuthUID))

    assertEquals(0, updatedEvent.polls[poll.id]!!.votes[account0.firebaseAuthUID])
    assertEquals(0, updatedEvent.polls[poll.id]!!.votes[account1.firebaseAuthUID])
    assertEquals(1, updatedEvent.polls[poll.id]!!.votes[account2.firebaseAuthUID])

    assertEquals(2, updatedEvent.polls[poll.id]!!.getNumberOfVotesPerOption()[0])
    assertEquals(1, updatedEvent.polls[poll.id]!!.getNumberOfVotesPerOption()[1])

    loading = true
    eventManager.atomic.deletePoll(event.id, poll.id, { loading = false }, { assertTrue(false) })
    while (loading) {}

    loading = true
    eventManager.getEventById(
        event.id,
        {
          updatedEvent = it!!
          loading = false
        },
        { assertTrue(false) })
    while (loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertEquals(0, updatedEvent.polls.size)
  }
}
