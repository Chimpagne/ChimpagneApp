package com.monkeyteam.chimpagne.newtests

import androidx.test.platform.app.InstrumentationRegistry
import com.monkeyteam.chimpagne.model.location.GetWeatherConstants
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.getWeather
import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class WeatherTest {
  private lateinit var server: MockWebServer
  private val apiKey = "test_api_key"
  val context = InstrumentationRegistry.getInstrumentation().targetContext

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
    GetWeatherConstants.PORT = server.port
    GetWeatherConstants.SCHEME = "http"
    GetWeatherConstants.HOST = server.hostName
    GetWeatherConstants.VERSION = ""
    GetWeatherConstants.FORECAST = ""
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun testSuccessfulResponse() {
    val json =
        """
            {
                "location": {"lat": 52.5200, "lon": 13.4050},
                "forecast": {
                    "forecastday": [{
                        "day": {
                            "condition": {"text": "Sunny", "icon": "//cdn.weatherapi.com/weather/64x64/day/113.png"},
                            "mintemp_c": 10.0,
                            "maxtemp_c": 20.0,
                            "maxwind_kph": 15.0,
                            "wind_deg": "34"
                        }
                    }]
                }
            }
        """
            .trimIndent()
    server.enqueue(MockResponse().setBody(json).setResponseCode(200))
    val latch = CountDownLatch(1)

    val location = Location("Berlin", 52.5200, 13.4050)
    val date = LocalDate.now()

    getWeather(
        location,
        date,
        { weather ->
          Assert.assertEquals("Sunny", weather.weatherDescription)
          Assert.assertEquals("//cdn.weatherapi.com/weather/64x64/day/113.png", weather.weatherIcon)
          Assert.assertEquals(10.0, weather.temperatureLow, 0.001)
          Assert.assertEquals(20.0, weather.temperatureHigh, 0.001)
          Assert.assertEquals(15.0, weather.maxWindSpeed, 0.001)
          Assert.assertEquals(34, weather.windDirection)
          latch.countDown()
        },
        {
          Assert.fail("Expected successful response")
          latch.countDown()
        },
        context)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testApiResponseFailure() {
    server.enqueue(MockResponse().setResponseCode(500))
    val latch = CountDownLatch(1)

    getWeather(
        Location("Invalid", 0.0, 0.0),
        LocalDate.now(),
        { Assert.fail("Expected failure response") },
        { latch.countDown() },
        context)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testInvalidJsonResponse() {
    val invalidJson = "Not a JSON"
    server.enqueue(MockResponse().setBody(invalidJson).setResponseCode(200))
    val latch = CountDownLatch(1)

    getWeather(
        Location("InvalidJSON", 0.0, 0.0),
        LocalDate.now(),
        { Assert.fail("Expected failure due to invalid JSON") },
        { latch.countDown() },
        context)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testEmptyJsonResponse() {
    val emptyJson = "{}"
    server.enqueue(MockResponse().setBody(emptyJson).setResponseCode(200))
    val latch = CountDownLatch(1)

    getWeather(
        Location("EmptyJSON", 0.0, 0.0),
        LocalDate.now(),
        { Assert.fail("Expected failure due to empty JSON") },
        { latch.countDown() },
        context)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testNetworkFailure() {
    server.shutdown() // Simulate network failure
    val latch = CountDownLatch(1)

    getWeather(
        Location("NetworkFailure", 0.0, 0.0),
        LocalDate.now(),
        { Assert.fail("Expected network failure response") },
        { latch.countDown() },
        context)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }
}
