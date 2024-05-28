package com.monkeyteam.chimpagne.model.location

import java.time.LocalDate

/**
 * Weather data class filled up with data from the API
 * Then used to build the UI
 */
data class Weather(
    val location: Location? = Location(),
    val date: LocalDate = LocalDate.now(),
    val weatherDescription: String = "",
    val weatherIcon: String = "",
    val temperatureLow: Double = 0.0,
    val temperatureHigh: Double = 0.0,
    val maxWindSpeed: Double = 0.0,
    val windDirection: Int = -1
)
