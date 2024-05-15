package com.monkeyteam.chimpagne.newtests.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.model.utils.setCalendarToMidnight
import com.monkeyteam.chimpagne.ui.components.DateRangeSelector
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class DateRangeSelectorUITests {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun dateRangeSelectorTest() {
    val firstDate = buildCalendar(1, 1, 2025, 1, 0)
    val secondDate = buildCalendar(5, 1, 2025, 1, 0)

    composeTestRule.setContent {
      DateRangeSelector(startDate = firstDate, endDate = secondDate ) { a, b ->
        assertEquals(firstDate.timeInMillis, a.timeInMillis)
        assertEquals(secondDate.timeInMillis, b.timeInMillis)
      }
    }

    composeTestRule.onNodeWithTag("date_range_button").performClick()
    composeTestRule.onNodeWithTag("date_range_submit").performClick()

  }
}
