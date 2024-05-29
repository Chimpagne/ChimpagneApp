package com.monkeyteam.chimpagne.newtests.ui.event

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.Weather
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.newtests.TEST_EVENTS
import com.monkeyteam.chimpagne.ui.utilities.WeatherCarouselInternal
import com.monkeyteam.chimpagne.ui.utilities.WeatherPager
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindEventViewModelTests {

  val database = Database()

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testWeatherAppWithOldEvent() {
    val event = TEST_EVENTS[0]
    composeTestRule.setContent { WeatherPager(event) }
    // Check that the message is displayed
    composeTestRule.onNodeWithTag("weather message").assertIsDisplayed()
  }

  @Test
  fun testWeatherAppWithFutureEvent() {
    val event =
        ChimpagneEvent(
            id = "1",
            title = "1t",
            description = "1d",
            location = Location("EPFL", 46.519124, 6.567593),
            public = true,
            tags = listOf(),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = buildTimestamp(9, 5, 2050, 15, 15),
            endsAtTimestamp = buildTimestamp(11, 5, 2050, 15, 15),
            ownerId = "Athena",
            supplies = mapOf(),
            parkingSpaces = 1,
            beds = 1)

    composeTestRule.setContent { WeatherPager(event) }
    // Check that the message is displayed
    composeTestRule.onNodeWithTag("weather message").assertIsDisplayed()
  }

  @Test
  fun testLoadingState() {
    val event =
        ChimpagneEvent(
            id = "1",
            title = "1t",
            description = "1d",
            location = Location("EPFL", 46.519124, 6.567593),
            public = true,
            tags = listOf(),
            guests = emptyMap(),
            staffs = emptyMap(),
            startsAtTimestamp = Timestamp.now(),
            endsAtTimestamp = buildTimestamp(11, 5, 2030, 15, 15),
            ownerId = "Athenaaaa",
            supplies = mapOf(),
            parkingSpaces = 1,
            beds = 1)

    composeTestRule.setContent { WeatherPager(event) }
    // Check that the message is displayed
    composeTestRule.onNodeWithTag("loading_indicator").assertExists()
  }

  @Test
  fun testWeatherCarouselWithSingleWeatherData() {
    val weatherData =
        listOf(
            Weather(
                date = LocalDate.now(),
                weatherDescription = "Sunny",
                weatherIcon = "https://cdn.weatherapi.com/weather/64x64/day/113.png",
                temperatureLow = 15.0,
                temperatureHigh = 25.0,
                maxWindSpeed = 10.0,
                windDirection = 90))

    composeTestRule.setContent { WeatherCarouselInternal(weatherData = weatherData) }

    // Check that the weather card is displayed
    composeTestRule.onNodeWithTag("weather card").assertIsDisplayed()
    // Check that the temperature text is displayed
    composeTestRule.onNodeWithTag("weather temp").assertIsDisplayed()
    // Check that the wind speed text is displayed
    composeTestRule.onNodeWithTag("weather wind").assertIsDisplayed()
  }
}
