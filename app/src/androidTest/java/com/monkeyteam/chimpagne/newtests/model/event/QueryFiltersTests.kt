package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.happensInDateRangeFilter
import com.monkeyteam.chimpagne.model.database.onlyPublicFilter
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QueryFiltersTests {

  val database = Database()

  val events =
      listOf(
          ChimpagneEvent(
              id = "banana",
              public = true,
              location = Location("EPFL", 46.519124, 6.567593),
              startsAtTimestamp = buildTimestamp(10, 1, 2024, 10, 1),
              endsAtTimestamp = buildTimestamp(1, 1, 2025, 10, 1)))

  @Before
  fun init() {
    initializeTestDatabase(events)
  }

  @Test
  fun firstTest() {
    val filter =
        Filter.and(
            onlyPublicFilter(),
            happensInDateRangeFilter(buildCalendar(9, 1, 2024, 10, 1), events[0].startsAt()))

    var loading = true
    var fetchedEvents = listOf<ChimpagneEvent>()
    database.eventManager.getAllEventsByFilterAroundLocation(
        events[0].location,
        5000.0,
        {
          fetchedEvents = it
          loading = false
        },
        { assertTrue(false) },
        filter)
    while (loading) {}

    assertEquals(events, fetchedEvents)
  }
}
