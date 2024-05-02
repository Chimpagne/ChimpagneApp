package com.monkeyteam.chimpagne.newtests.model.event

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventTest {

  @Test
  fun guestListAndStaffListTest() {
    val event =
        ChimpagneEvent(
            staffs = hashMapOf("1" to true, "2" to true), guests = hashMapOf("3" to true))
    assertEquals(event.guests.keys, event.guestList())
    assertEquals(event.staffs.keys, event.staffList())
  }
}
