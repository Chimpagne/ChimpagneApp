package com.monkeyteam.chimpagne

import com.google.android.gms.maps.model.LatLng
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.utilities.MarkerData
import com.monkeyteam.chimpagne.ui.utilities.SingletonCluster
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SingletonClusterTest {

  private lateinit var markerData: MarkerData
  private lateinit var singletonCluster: SingletonCluster

  @Before
  fun setUp() {
    markerData = MarkerData("test", "test", Location("test"))
    singletonCluster = SingletonCluster(markerData)
  }

  @Test
  fun testGetItems() {
    val items = singletonCluster.getItems()
    assertEquals(1, items.size)
    assertEquals(markerData, items.first())
  }

  @Test
  fun testGetSize() {
    val size = singletonCluster.size
    assertEquals(1, size)
  }

  @Test
  fun testGetPosition() {
    val position = singletonCluster.position
    assertEquals(LatLng(markerData.location.latitude, markerData.location.longitude), position)
  }
}
