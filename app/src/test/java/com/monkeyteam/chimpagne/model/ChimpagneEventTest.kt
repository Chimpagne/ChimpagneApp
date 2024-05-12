package com.monkeyteam.chimpagne.model

import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ChimpagneEventTest {

  @Test
  fun userSetTest() {
    val event = ChimpagneEvent(ownerId = "banana", staffs = mapOf("1" to true, "2" to true, "3" to true), guests = mapOf("4" to true))
    assertEquals(setOf("banana", "1", "2", "3", "4"), event.getUserSet())
  }
}