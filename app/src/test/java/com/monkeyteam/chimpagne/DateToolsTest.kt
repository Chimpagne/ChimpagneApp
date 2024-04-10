package com.monkeyteam.chimpagne

import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import java.util.Calendar
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateToolsTest {
  @Test
  fun date_tools_isCorrect() {
    val calendar = buildCalendar(1, 5, 2024, 5, 1)
    assertEquals(calendar.get(Calendar.DATE), 1)
    assertEquals(calendar.get(Calendar.MONTH), 5)
    assertEquals(calendar.get(Calendar.YEAR), 2024)
    assertEquals(calendar.get(Calendar.HOUR), 5)
    assertEquals(calendar.get(Calendar.MINUTE), 1)

    val timestamp = buildTimestamp(calendar)
    val cal = buildCalendar(timestamp)
    assertEquals(cal.get(Calendar.DATE), 1)
    assertEquals(cal.get(Calendar.MONTH), 5)
    assertEquals(cal.get(Calendar.YEAR), 2024)
    assertEquals(cal.get(Calendar.HOUR), 5)
    assertEquals(cal.get(Calendar.MINUTE), 1)

    val time = buildTimestamp(4, 5, 2022, 6, 0)
    val cal2 = buildCalendar(time)
    assertEquals(cal2.get(Calendar.DATE), 4)
    assertEquals(cal2.get(Calendar.MONTH), 5)
    assertEquals(cal2.get(Calendar.YEAR), 2022)
    assertEquals(cal2.get(Calendar.HOUR), 6)
    assertEquals(cal2.get(Calendar.MINUTE), 0)
  }
}
