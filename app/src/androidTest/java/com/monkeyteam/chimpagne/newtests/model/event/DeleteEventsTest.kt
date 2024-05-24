package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteEventsTests {

  val database = Database()
  val accountManager = database.accountManager
  val eventManager = database.eventManager

  val anAccount = TEST_ACCOUNTS[0] // PRINCE
  val anotherAccount = TEST_ACCOUNTS[1] // JUAN
  val thirdAccount = TEST_ACCOUNTS[2] // THEREALKING

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @Test
  fun deleteAllRelatedEventsSuccess() {
    accountManager.signInTo(anAccount)

    var loading = true
    eventManager.deleteAllRelatedEvents(
        anAccount.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    while (loading) {}

    // Check events owned by PRINCE are deleted
    var e: ChimpagneEvent? = null
    eventManager.getEventById("LOTR", { e = it }, { assertTrue(false) })
    assertEquals(null, e)

    eventManager.getEventById("THIRD_EVENT", { e = it }, { assertTrue(false) })
    assertEquals(null, e)

    // Check PRINCE is removed from guests/staffs
    eventManager.getEventById("FOURTH_EVENT", { e = it }, { assertTrue(false) })
    while (e == null) {}
    var event = e!!
    assertEquals(false, event.guests.containsKey(anAccount.firebaseAuthUID))
    assertEquals(false, event.staffs.containsKey(anAccount.firebaseAuthUID))
  }

  @Test
  fun deleteAllRelatedEventsPartialSuccess() {
    accountManager.signInTo(thirdAccount)

    var loading = true
    eventManager.deleteAllRelatedEvents(
        thirdAccount.firebaseAuthUID, { loading = false }, { assertTrue(false) })
    while (loading) {}

    // Check events owned by THEREALKING are not deleted
    var e: ChimpagneEvent? = null
    eventManager.getEventById("FOURTH_EVENT", { e = it }, { assertTrue(false) })
    while (e == null) {}
    var event = e!!
    assertEquals("JUAN", event.ownerId)
    assertEquals(false, event.staffs.containsKey(thirdAccount.firebaseAuthUID))
    assertEquals(false, event.guests.containsKey(thirdAccount.firebaseAuthUID))
  }
}
