package com.monkeyteam.chimpagne

import com.monkeyteam.chimpagne.model.location.NominatimConstants
import com.monkeyteam.chimpagne.model.location.convertNameToLocations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NominatimTests {

  private lateinit var server: MockWebServer

  // BACKEND tests

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
    val url = server.url("/")
    NominatimConstants.HOST = url.host
    NominatimConstants.PORT = url.port
    NominatimConstants.SCHEME = url.scheme
  }

  @After
  fun tearDown() {
    server.shutdown()
    NominatimConstants.HOST = "nominatim.openstreetmap.org"
  }

  @Test
  fun testSuccessfulResponse() {
    val json =
        """[{"lat": 52.5200, "lon": 13.4050, "address": {"country": "Germany", "city": "Berlin"}, "category": "city"}]"""
    server.enqueue(MockResponse().setBody(json).setResponseCode(200))
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "Berlin",
        { locations ->
          Assert.assertTrue("Expected non-empty list of locations", locations.isNotEmpty())
          Assert.assertEquals("Latitude should match", 52.5200, locations.first().latitude, 0.001)
          Assert.assertEquals("Longitude should match", 13.4050, locations.first().longitude, 0.001)
          latch.countDown()
        },
        limit = 5)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testCategoryBuilding() {
    val json =
        """[{
        "place_id": 123,
        "category": "building",
        "type": "office",
        "address": {
            "country": "USA",
            "postcode": "90210",
            "city": "Beverly Hills",
            "road": "Wilshire Boulevard",
            "house_number": "123",
            "building": "Empire State"
        },
        "lat": "34.069",
        "lon": "-118.406"
    }]"""

    server.enqueue(MockResponse().setBody(json).setResponseCode(200))
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "Empire State",
        { locations ->
          Assert.assertTrue(locations.isNotEmpty())
          Assert.assertEquals(
              "Wilshire Boulevard, 123, 90210, Beverly Hills, USA", locations.first().name)
          Assert.assertEquals(34.069, locations.first().latitude, 0.001)
          Assert.assertEquals(-118.406, locations.first().longitude, 0.001)
          latch.countDown()
        },
        1)

    Assert.assertTrue(latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testCategoryAmenity() {
    val json =
        """[{
        "place_id": 456,
        "category": "amenity",
        "type": "university",
        "address": {
            "country": "Schweiz/Suisse/Svizzera/Svizra",
            "postcode": "1015",
            "city": "Lausanne",
            "road": "Route de la Sorge",
            "amenity": "EPFL"
        },
        "lat": "46.519",
        "lon": "6.566"
    }]"""

    server.enqueue(MockResponse().setBody(json).setResponseCode(200))
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "EPFL",
        { locations ->
          Assert.assertTrue("Expected non-empty list of locations", locations.isNotEmpty())
          Assert.assertEquals("EPFL, Lausanne, 1015, Switzerland", locations.first().name)
          Assert.assertEquals(46.519, locations.first().latitude, 0.001)
          Assert.assertEquals(6.566, locations.first().longitude, 0.001)
          latch.countDown()
        },
        limit = 5)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testApiResponseFailure() {
    server.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "Invalid",
        { locations ->
          Assert.assertTrue("Expected empty locations list on API failure", locations.isEmpty())
          latch.countDown()
        },
        limit = 5)

    Assert.assertTrue("Test timed out waiting for callback", latch.await(5, TimeUnit.SECONDS))
  }

  @Test
  fun testNetworkFailure() {
    server.shutdown() // Immediately shut down to simulate a network failure
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "Shutdown",
        { locations ->
          Assert.assertTrue(
              "The locations list should be empty when there is a network failure",
              locations.isEmpty())
          latch.countDown()
        },
        5)

    Assert.assertTrue(
        "Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testEmptyResponse() {
    val json = "[]"
    server.enqueue(MockResponse().setBody(json).setResponseCode(200))
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "EmptyResponse",
        { locations ->
          Assert.assertTrue("Expected an empty list of locations", locations.isEmpty())
          latch.countDown()
        },
        limit = 5)

    Assert.assertTrue(latch.await(2, TimeUnit.SECONDS))
  }

  @Test
  fun testInvalidJsonResponse() {
    val invalidJson = "Not a JSON"
    server.enqueue(MockResponse().setBody(invalidJson).setResponseCode(200))
    val latch = CountDownLatch(1)

    convertNameToLocations(
        "InvalidJSON",
        { locations ->
          Assert.assertTrue(
              "Expected an empty list of locations due to invalid JSON", locations.isEmpty())
          latch.countDown()
        },
        limit = 5)

    Assert.assertTrue(latch.await(2, TimeUnit.SECONDS))
  }
}
