package com.monkeyteam.chimpagne

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import java.util.Calendar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarTests() {

  @get:Rule val composeTestRule = createComposeRule()

  val calendarBegin: Calendar =
      Calendar.getInstance().apply { set(2024, Calendar.MAY, 20, 10, 30, 0) }
  val secondBegin = calendarBegin.timeInMillis / 1000
  val timestampBegin = Timestamp(secondBegin, 0)

  val calendarEnd = Calendar.getInstance().apply { set(2024, Calendar.MAY, 21, 9, 30, 0) }
  val secondEnd = calendarEnd.timeInMillis / 1000
  val timestampEnd = Timestamp(secondEnd, 0)

  val ChimpagneEvent =
      ChimpagneEvent(
          id = "1",
          title = "Test Event",
          description = "Test Description",
          location = Location("Test Location", 42.3, 6.8),
          public = true,
          tags = listOf("Test Tag"),
          guests = hashMapOf("1" to true),
          staffs = hashMapOf("1" to true),
          startsAtTimestamp = timestampBegin,
          endsAtTimestamp = timestampEnd,
          ownerId = "1",
          supplies = mapOf("1" to ChimpagneSupply()),
          parkingSpaces = 1,
          beds = 1)

  @Test
  fun testCalendarIntent() {
    var intentToLaunch: Intent? = null
    composeTestRule.setContent {
      ChimpagneButton(
          text = "Add to Calendar",
          icon = Icons.Default.CalendarMonth,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          onClick = { intentToLaunch = createCalendarIntent(ChimpagneEvent) },
          backgroundColor = MaterialTheme.colorScheme.primary,
          shape = RoundedCornerShape(12.dp),
          padding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
          modifier = Modifier.testTag("calendarButton"))
    }

    composeTestRule.onNodeWithTag("calendarButton").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("rejectButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("acceptButton").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("calendarButton").performClick()
  }

  @Test
  fun testCalendarButton() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navActions = NavigationActions(navController)
      val database = Database()
      val eventViewModel = EventViewModel(database = database)
      ViewDetailEventScreen(navObject = navActions, eventViewModel = eventViewModel)
    }

    composeTestRule.onNodeWithTag("calendarButton").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("calendarButton").performClick()
  }

  @Test
  fun testCalendarPopUpYes() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    var accepted = false
    var rejected = false
    val event =
        ChimpagneEvent(
            id = "1",
            title = "Test Event",
            description = "Test Description",
            location = Location("Test Location", 42.3, 6.8),
            public = true,
            tags = listOf("Test Tag"),
            guests = hashMapOf("1" to true),
            staffs = hashMapOf("1" to true),
            startsAtTimestamp = Timestamp(0, 0),
            endsAtTimestamp = Timestamp(0, 0),
            ownerId = "1",
            supplies = mapOf("1" to ChimpagneSupply()),
            parkingSpaces = 1,
            beds = 1)

    composeTestRule.setContent {
      popUpCalendar(onAccept = { accepted = true }, onReject = { rejected = true }, event = event)
    }
    val textDisplayed =
        context.getString(R.string.add_event_to_calendar_prefix) +
            event.title +
            context.getString(R.string.add_event_to_calendar_suffix)

    composeTestRule.onNodeWithText("Add to Calendar").assertIsDisplayed()
    composeTestRule.onNodeWithText(textDisplayed).assertIsDisplayed()
    composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
    composeTestRule.onNodeWithText("No").assertIsDisplayed()

    composeTestRule.onNode(hasText("Yes")).performClick()
    assert(accepted)
  }

  @Test
  fun testCalendarPopUpNo() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    var accepted = false
    var rejected = false
    val event =
        ChimpagneEvent(
            id = "1",
            title = "Test Event",
            description = "Test Description",
            location = Location("Test Location", 42.3, 6.8),
            public = true,
            tags = listOf("Test Tag"),
            guests = hashMapOf("1" to true),
            staffs = hashMapOf("1" to true),
            startsAtTimestamp = Timestamp(0, 0),
            endsAtTimestamp = Timestamp(0, 0),
            ownerId = "1",
            supplies = mapOf("1" to ChimpagneSupply()),
            parkingSpaces = 1,
            beds = 1)

    composeTestRule.setContent {
      popUpCalendar(onAccept = { accepted = true }, onReject = { rejected = true }, event = event)
    }
    val textDisplayed =
        context.getString(R.string.add_event_to_calendar_prefix) +
            event.title +
            context.getString(R.string.add_event_to_calendar_suffix)

    composeTestRule.onNodeWithText("Add to Calendar").assertIsDisplayed()
    composeTestRule.onNodeWithText(textDisplayed).assertIsDisplayed()
    composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
    composeTestRule.onNodeWithText("No").assertIsDisplayed()

    composeTestRule.onNode(hasText("No")).performClick()
    assert(rejected)
  }

  @Test
  fun testCalendarButtonIntentNo() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val event = ChimpagneEvent
    composeTestRule.setContent { CalendarButton(event = event, contextMainActivity = context) }

    composeTestRule.onNodeWithTag("calendarButton").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("rejectButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("acceptButton").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("calendarButton").performClick()

    composeTestRule.onNodeWithTag("acceptButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("rejectButton").assertIsDisplayed()

    composeTestRule.onNodeWithTag("rejectButton").performClick()

    composeTestRule.onNodeWithTag("acceptButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("rejectButton").assertIsNotDisplayed()
  }
}
