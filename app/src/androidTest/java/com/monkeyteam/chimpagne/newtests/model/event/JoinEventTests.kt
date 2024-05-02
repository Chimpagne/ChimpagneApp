package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.NotLoggedInException
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinEventTests {

  val database = Database()
  val accountManager = database.accountManager
  val eventManager = database.eventManager

  val anAccount = TEST_ACCOUNTS[1]

  val anotherAccount = TEST_ACCOUNTS[2]

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @Test
  fun joinEventTest1() {
    accountManager.signInTo(anAccount)

    var eventId = ""
    eventManager.createEvent(
        ChimpagneEvent(title = "Banana Land", ownerId = anAccount.firebaseAuthUID),
        { eventId = it },
        { assertTrue(false) })
    while (eventId == "") {}
    var e: ChimpagneEvent? = null
    eventManager.getEventById(eventId, { e = it }, { assertTrue(false) })
    while (e == null) {}
    var event = e!!

    assertEquals(anAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(anAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.NOT_IN_EVENT, event.getRole(anotherAccount.firebaseAuthUID))

    var loading = true
    eventManager.addGuest(
        eventId,
        anotherAccount.firebaseAuthUID,
        { loading = false },
        {
          loading = false
          assertTrue(false)
        })
    while (loading) {}
    e = null
    eventManager.getEventById(eventId, { e = it }, { assertTrue(false) })
    while (e == null) {}
    event = e!!

    assertEquals(anAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(anAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.GUEST, event.getRole(anotherAccount.firebaseAuthUID))

    accountManager.signInTo(anotherAccount)
    loading = true
    accountManager.joinEvent(
        eventId, ChimpagneRole.STAFF, { loading = false }, { assertTrue(false) })
    while (loading) {}
    e = null
    eventManager.getEventById(eventId, { e = it }, { assertTrue(false) })
    while (e == null) {}
    event = e!!

    assertEquals(anAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(anAccount.firebaseAuthUID))
    assertTrue(ChimpagneRole.GUEST != event.getRole(anotherAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.STAFF, event.getRole(anotherAccount.firebaseAuthUID))
  }

  @Test
  fun joinEventTest2() {
    var event = TEST_EVENTS[0]

    var loading = true
    accountManager.joinEvent(
        event.id,
        ChimpagneRole.GUEST,
        {
          assertTrue(false)
          loading = false
        },
        {
          assertEquals(NotLoggedInException().message, it.message)
          loading = false
        })

    while (loading) {}

    accountManager.signInTo(anotherAccount)

    loading = true
    accountManager.joinEvent(
        event.id, ChimpagneRole.GUEST, { loading = false }, { assertTrue(false) })
    while (loading) {}
    var e: ChimpagneEvent? = null
    eventManager.getEventById(event.id, { e = it }, { assertTrue(false) })
    while (e == null) {}
    event = e!!

    assertEquals(anAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(anAccount.firebaseAuthUID))
    assertTrue(ChimpagneRole.STAFF != event.getRole(anotherAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.GUEST, event.getRole(anotherAccount.firebaseAuthUID))

    loading = true
    accountManager.joinEvent(
        event.id,
        ChimpagneRole.NOT_IN_EVENT,
        {
          loading = false
          assertTrue(false)
        },
        { loading = false })
    while (loading) {}
  }
}
