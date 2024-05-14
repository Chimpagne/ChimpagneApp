package com.monkeyteam.chimpagne

import DateSelector
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.SocialButton
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
import com.monkeyteam.chimpagne.ui.components.SupportedSocialMedia
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.GoogleAuthentication
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleUtilitiesUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkUI() {

    composeTestRule.setContent { GoogleAuthentication({}, {}) }

    composeTestRule.onNodeWithTag("googleAuthenticationButton").assertTextContains(getSignInText())
    composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()
  }

  // Both todo with dependency injections
  @Test fun checkFailedLogin() {}

  @Test fun checkSuccessfulLogin() {}
}

@RunWith(AndroidJUnit4::class)
class ThemeUITest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test fun checkLightTheme() {}
}

@RunWith(AndroidJUnit4::class)
class DateSelectorTest {

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun DatePickerWorks() {
    runBlocking {
      val selectedDate = Calendar.getInstance()
      var chosenDate: Calendar? = null
      composeTestRule.setContent {
        DateSelector(selectedDate = selectedDate, onDateSelected = { chosenDate = it })
      }

      composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
      composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()

      composeTestRule.onNodeWithTag("dateSelector").assertIsDisplayed()
      composeTestRule.onNodeWithTag("dateSelector").performClick()

      composeTestRule.onNodeWithTag("selectDate").assertIsDisplayed()
      composeTestRule.onNodeWithTag("selectDate").performClick()

      composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()

      assertIsWithinOne(chosenDate!!.get(Calendar.YEAR), selectedDate.get(Calendar.YEAR))
      assertIsWithinOne(chosenDate!!.get(Calendar.MONTH), selectedDate.get(Calendar.MONTH))
      assertIsWithinOne(chosenDate!!.get(Calendar.DATE), selectedDate.get(Calendar.DATE))
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun TimePickerWorks() {
    runBlocking {
      val selectedDate = Calendar.getInstance()
      var chosenDate: Calendar? = null
      composeTestRule.setContent {
        DateSelector(
            selectedDate = selectedDate,
            onDateSelected = { chosenDate = it },
            selectTimeOfDay = true)
      }

      composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
      composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()

      composeTestRule.onNodeWithTag("dateSelector").assertIsDisplayed()
      composeTestRule.onNodeWithTag("dateSelector").performClick()

      composeTestRule.onNodeWithTag("selectDate").assertIsDisplayed()
      composeTestRule.onNodeWithTag("selectDate").performClick()

      composeTestRule.onNodeWithTag("selectDate").assertIsNotDisplayed()
      composeTestRule.onNodeWithTag("selectTime").assertIsDisplayed()
      composeTestRule.onNodeWithTag("selectTime").performClick()

      composeTestRule.onNodeWithTag("selectTime").assertIsNotDisplayed()
      val currentDate = Calendar.getInstance()
      assertIsWithinOne(chosenDate!!.get(Calendar.YEAR), selectedDate.get(Calendar.YEAR))
      assertIsWithinOne(chosenDate!!.get(Calendar.MONTH), selectedDate.get(Calendar.MONTH))
      assertIsWithinOne(chosenDate!!.get(Calendar.DATE), selectedDate.get(Calendar.DATE))
      assertIsWithinOne(chosenDate!!.get(Calendar.HOUR), currentDate.get(Calendar.HOUR))
      assertIsWithinOne(chosenDate!!.get(Calendar.MINUTE), currentDate.get(Calendar.MINUTE))
    }
  }
}

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkUI() {
    composeTestRule.setContent {
      val mockSuccessfulLogin: (String) -> Unit = { _ -> }
      val mockContinueAsGuest: () -> Unit = {}

      LoginScreen(onSuccessfulLogin = mockSuccessfulLogin, onContinueAsGuest = mockContinueAsGuest)
    }

    composeTestRule.onNodeWithTag("welcome_screen_title").assertExists()

    composeTestRule.onNodeWithTag("Chimpagne").assertExists()

    composeTestRule.onNodeWithTag("Google_Authentication").assertExists()

    composeTestRule.onNodeWithTag("Continue_As_Guest_Button").assertExists()
  }

  @Test fun checkAlertDialog() {}
}

private fun assertIsWithinOne(toBeCompared: Int, expected: Int) {
  assert(toBeCompared == expected || toBeCompared == expected + 1 || toBeCompared == expected - 1)
}

private fun getWelcomeScreenText(): String {
  val currentLocale = Locale.getDefault()
  return if (currentLocale.language == "fr") {
    "Bienvenue à"
  } else {
    "Welcome to"
  }
}

private fun getSignInText(): String {
  val currentLocale = Locale.getDefault()
  return if (currentLocale.language == "fr") {
    "Se connecter avec Google"
  } else {
    "Sign in with Google"
  }
}

class ProfileIconTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profileIcon_withUri_showsImage() {
    val testUri = Uri.parse("https://example.com/profile.jpg")
    composeTestRule.setContent { ProfileIcon(uri = testUri, onClick = {}) }
    composeTestRule.onNodeWithContentDescription("Profile").assertExists()
  }

  @Test
  fun profileIcon_withNullUri_showsDefaultImage() {
    composeTestRule.setContent { ProfileIcon(uri = null, onClick = {}) }
    composeTestRule.onNodeWithContentDescription("Profile").assertExists()
  }

  @Test
  fun profileIcon_click_performsAction() {
    var clicked = false
    composeTestRule.setContent { ProfileIcon(uri = null) { clicked = true } }
    composeTestRule.onNodeWithContentDescription("Profile").performClick()

    assert(clicked)
  }
}

@RunWith(Enclosed::class)
class TestCalendar() {

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

    composeTestRule.onNodeWithText("Add to Calendar").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Do you want to add the event \"${event.title}\" to your calendar?")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
    composeTestRule.onNodeWithText("No").assertIsDisplayed()

    composeTestRule.onNode(hasText("Yes")).performClick()
    assert(accepted)
  }

  @Test
  fun testCalendarPopUpNo() {
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

    composeTestRule.onNodeWithText("Add to Calendar").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Do you want to add the event \"${event.title}\" to your calendar?")
        .assertIsDisplayed()
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

class TestSocialButtonConstructs {

  @get:Rule val composeTestRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Test
  fun testSocialButton() {
    composeTestRule.setContent {
      SocialButton(
          imageLogo = R.drawable.discord,
          urlAsString = "https://discord.gg/",
          context = context,
          testTag = "Discord_Button")
    }

    composeTestRule.onNodeWithTag("Discord_Button").assertExists().isDisplayed()
  }

  @Test
  fun AllThreeDisplayed() {
    val mapSocialMedia = SupportedSocialMedia.associateBy { it.platformName }
    val filledInSocialMedia =
        mapSocialMedia.mapValues { it.value.copy(chosenGroupUrl = it.value.platformUrl) }
    composeTestRule.setContent {
      SocialButtonRow(context = context, socialMediaLinks = filledInSocialMedia)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("discord_input").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("whatsapp_input").assertExists().isDisplayed()
    composeTestRule.onNodeWithTag("telegram_input").assertExists().isDisplayed()
  }

  @Test
  fun OnlyTelegramDisplayed() {
    val socialMediaMap = SupportedSocialMedia.associateBy { it.platformName }
    val telegramMap =
        mapOf(socialMediaMap["telegram"]!!.platformName to socialMediaMap["telegram"]!!)
    val withValueMap =
        telegramMap.mapValues { it.value.copy(chosenGroupUrl = it.value.platformUrl) }
    composeTestRule.setContent {
      SocialButtonRow(context = context, socialMediaLinks = withValueMap)
    }
    composeTestRule.onNodeWithTag("discord_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("whatsapp_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("telegram_input").assertExists().isDisplayed()
  }

  @Test
  fun NoneDisplayed() {
    composeTestRule.setContent { SocialButtonRow(context = context, socialMediaLinks = emptyMap()) }
    composeTestRule.onNodeWithTag("discord_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("whatsapp_input").assertDoesNotExist()
    composeTestRule.onNodeWithTag("telegram_input").assertDoesNotExist()
  }
}
