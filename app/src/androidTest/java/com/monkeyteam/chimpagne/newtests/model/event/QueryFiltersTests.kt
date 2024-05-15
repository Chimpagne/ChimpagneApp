package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.happensOnDayFiler
import com.monkeyteam.chimpagne.model.database.onlyPublicFilter
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueryFiltersTests {

  val database = Database()

  @Before
  fun init() {
    initializeTestDatabase()
  }

  @Test
  fun firstTest() {
    val day = buildCalendar(9, 5, 2030, 15, 15)
    val filter = Filter.and(onlyPublicFilter(), happensOnDayFiler(day))

    var loading = true
    var events = listOf<ChimpagneEvent>()
    database.eventManager.getAllEventsByFilterAroundLocation(
        TEST_EVENTS[0].location,
        5000.0,
        {
          events = it
          loading = false
        },
        { assertTrue(false) },
        filter)
    while (loading) {}

    assertEquals(listOf<ChimpagneEvent>(), events)
  }
}
