package com.monkeyteam.chimpagne.newtests.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.ui.components.DateRangeSelector
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateRangeSelectorUITests {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun dateRangeSelectorTest() {
    val firstDate = buildCalendar(1, 1, 2025, 1, 0)
    val secondDate = buildCalendar(5, 1, 2025, 1, 0)

    composeTestRule.setContent {
      DateRangeSelector(startDate = firstDate, endDate = secondDate) { a, b ->
        assertTrue(b.timeInMillis >= a.timeInMillis)
      }
    }

    composeTestRule.onNodeWithTag("date_range_button").performClick()
    composeTestRule.onNodeWithTag("date_range_button").performClick() // performs the dismiss request
  }
}
