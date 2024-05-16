package com.monkeyteam.chimpagne.model

import com.monkeyteam.chimpagne.model.location.Location
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationTest {

  @Test
  fun testDistanceTo_sameLocation() {
    val loc1 = Location("Same Place", 52.5200, 13.4050) // Berlin
    val loc2 = Location("Same Place", 52.5200, 13.4050) // Berlin
    assertEquals(0.0, loc1.distanceTo(loc2), 0.0)
  }

  @Test
  fun testDistanceTo_shortDistance() {
    val loc1 = Location("Point A", 37.7749, -122.4194) // San Francisco
    val loc2 = Location("Point B", 37.8044, -122.2712) // Oakland
    val expectedDistance = 13.5 // Approximate distance in kilometers
    assertEquals(expectedDistance, loc1.distanceTo(loc2), 0.1)
  }

  @Test
  fun testDistanceTo_longDistance() {
    val loc1 = Location("Tokyo", 35.6895, 139.6917) // Tokyo
    val loc2 = Location("New York", 40.7128, -74.0060) // New York
    val expectedDistance = 10848.07 // Known distance in kilometers
    assertEquals(expectedDistance, loc1.distanceTo(loc2), 1.0)
  }

  @Test
  fun testDistanceTo_hemisphereDistance() {
    val loc1 = Location("North Pole", 90.0, 0.0) // North Pole
    val loc2 = Location("South Pole", -90.0, 0.0) // South Pole
    val expectedDistance =
        20015.0 // Approximate distance in kilometers (half Earth's circumference)
    assertEquals(expectedDistance, loc1.distanceTo(loc2), 1.0)
  }
}
