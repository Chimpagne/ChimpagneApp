package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinEventTests {

  val database = Database()
  val accountManager = database.accountManager
  val eventManager = database.eventManager

  val loggedAccount = TEST_ACCOUNTS[0]
  val anotherAccount = TEST_ACCOUNTS[2]

  @Before
  fun init() {
    initializeTestDatabase()
    accountManager.signInTo(loggedAccount)
  }

  @Test
  fun test() {
    var eventId = ""
    eventManager.createEvent(
      ChimpagneEvent(
        title = "Banana Land"
      ), { eventId = it }, { assertTrue(false) }
    )
    while (eventId == "") {
    }
    var e: ChimpagneEvent? = null;
    eventManager.getEventById(eventId, {e = it}, { assertTrue(false) })
    while (e == null) {
    }
    var event = e!!

    assertEquals(loggedAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(loggedAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.NOT_IN_EVENT, event.getRole(anotherAccount.firebaseAuthUID))

    var loading = true
    eventManager.addGuest(eventId, anotherAccount.firebaseAuthUID, {loading = false}, { loading = false; assertTrue(false) })
    while (loading) {}
    e = null;
    eventManager.getEventById(eventId, {e = it}, { assertTrue(false) })
    while (e == null) {
    }
    event = e!!

    assertEquals(loggedAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(loggedAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.GUEST, event.getRole(anotherAccount.firebaseAuthUID))

    accountManager.signInTo(anotherAccount)
    loading = true
    accountManager.joinEvent(eventId, ChimpagneRole.STAFF, {loading = false}, { assertTrue(false) })
    while (loading) {}
    e = null;
    eventManager.getEventById(eventId, {e = it}, { assertTrue(false) })
    while (e == null) {
    }
    event = e!!

    assertEquals(loggedAccount.firebaseAuthUID, event.ownerId)
    assertEquals(ChimpagneRole.OWNER, event.getRole(loggedAccount.firebaseAuthUID))
    assertTrue(ChimpagneRole.GUEST != event.getRole(anotherAccount.firebaseAuthUID))
    assertEquals(ChimpagneRole.STAFF, event.getRole(anotherAccount.firebaseAuthUID))
  }

}