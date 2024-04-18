package com.monkeyteam.chimpagne

import com.monkeyteam.chimpagne.model.location.NominatimConstants
import com.monkeyteam.chimpagne.model.location.convertNameToLocations
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class LocationSelectorTest {

    private lateinit var server: MockWebServer

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
        val json = """[{"lat": 52.5200, "lon": 13.4050, "address": {"country": "Germany", "city": "Berlin"}, "category": "city"}]"""
        server.enqueue(MockResponse().setBody(json).setResponseCode(200))
        val latch = CountDownLatch(1)

        convertNameToLocations("Berlin", { locations ->
            assertTrue("Expected non-empty list of locations", locations.isNotEmpty())
            assertEquals("Latitude should match", 52.5200, locations.first().latitude, 0.001)
            assertEquals("Longitude should match", 13.4050, locations.first().longitude, 0.001)
            latch.countDown()
        }, limit = 5)

        assertTrue("Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
    }

    @Test
    fun testApiResponseFailure() {
        server.enqueue(MockResponse().setResponseCode(500)) // Simulate API failure
        val latch = CountDownLatch(1)

        convertNameToLocations("Invalid", { locations ->
            assertTrue("Expected empty locations list on API failure", locations.isEmpty())
            latch.countDown()
        }, limit = 5)

        assertTrue("Test timed out waiting for callback", latch.await(5, TimeUnit.SECONDS))
    }

    @Test
    fun testNetworkFailure() {
        server.shutdown() // Immediately shut down to simulate a network failure
        val latch = CountDownLatch(1)

        convertNameToLocations("Shutdown", { locations ->
            assertTrue("The locations list should be empty when there is a network failure", locations.isEmpty())
            latch.countDown()
        }, 5)

        assertTrue("Callback was not invoked within the timeout period", latch.await(2, TimeUnit.SECONDS))
    }
}