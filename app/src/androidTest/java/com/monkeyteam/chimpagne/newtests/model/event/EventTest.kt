package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
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
}
