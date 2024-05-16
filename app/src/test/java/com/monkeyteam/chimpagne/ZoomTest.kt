package com.monkeyteam.chimpagne

import com.monkeyteam.chimpagne.ui.utilities.getZoomLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomTest {

    @Test
    fun testGetZoomLevel() {
        val radius = 10.0
        val zoomLevel = getZoomLevel(radius)
        assertEquals(20.7369, zoomLevel.toDouble(), 0.001)
    }

}