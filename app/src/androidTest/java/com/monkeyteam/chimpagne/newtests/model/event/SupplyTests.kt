package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.newtests.createEventAndWait
import com.monkeyteam.chimpagne.newtests.dropTestDatabase
import com.monkeyteam.chimpagne.newtests.initializeTestDatabase
import com.monkeyteam.chimpagne.newtests.updateEventAndWait
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SupplyTests {

  val database = Database()

  var testEvent = ChimpagneEvent("yo", supplies = hashMapOf("1" to ChimpagneSupply("1")))
  val supply = ChimpagneSupply("ok", "BANANA", 4, "bananas")

  @Before
  fun init() {
    dropTestDatabase()
    updateEventAndWait(database, testEvent)
  }

  @Test
  fun supplyTest1() {


    var loading = true
    database.eventManager.atomic.updateSupply(testEvent.id, supply, { loading = false }, { assertTrue(false) } )
    while (loading) {}

    var event: ChimpagneEvent? = null
    loading = true
    database.eventManager.getEventById("yo", { event = it; loading = false }, { assertTrue(false) } )
    while (loading) {}

    te

    assertEquals(testEvent.copy(su))
  }
}
