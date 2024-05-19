package com.monkeyteam.chimpagne.newtests.viewmodels

import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.SLEEP_AMOUNT_MILLIS
import com.monkeyteam.chimpagne.newtests.TEST_ACCOUNTS
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class MyEventsViewModelTests {
  val database = Database()

  private val testAccount1 = TEST_ACCOUNTS[0] /*account with create and join event*/
  private val testAccount2 = TEST_ACCOUNTS[1] /*account with no create and no join event*/

  private val joinedEventForAccount1 = TEST_EVENTS[2]
  private val createdEventForAccount1 = TEST_EVENTS[3]

  @Before
  fun initTests() {
    initializeTestDatabase()
  }

  @Test
  fun fetchingGuestEventsWithCreatedAndJoinedEvents() {
    database.accountManager.signInTo(testAccount1)

    val eventVM = MyEventsViewModel(database, { assertTrue(true) }, { assertTrue(false) })

    while (eventVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventVM.uiState.value.createdEvents.size == 1)
    assertTrue(
        eventVM.uiState.value.createdEvents[createdEventForAccount1.id]!!.id ==
            createdEventForAccount1.id)
    assertTrue(eventVM.uiState.value.joinedEvents.size == 2)
    assertTrue(
        eventVM.uiState.value.joinedEvents[joinedEventForAccount1.id]!!.id ==
            joinedEventForAccount1.id)
  }

  @Test
  fun fetchingGuestEventsWithNoEvents() {
    database.accountManager.signInTo(testAccount2)

    val eventVM = MyEventsViewModel(database, { assertTrue(true) }, { assertTrue(false) })

    while (eventVM.uiState.value.loading) {}
    Thread.sleep(SLEEP_AMOUNT_MILLIS)

    assertTrue(eventVM.uiState.value.createdEvents.isEmpty())
    assertTrue(eventVM.uiState.value.joinedEvents.isEmpty())
  }
}
