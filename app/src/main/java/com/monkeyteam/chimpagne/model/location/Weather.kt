package com.monkeyteam.chimpagne.model.location

data class Weather(
    val location: Location? = Location(),
    val weatherDescription: String = "",
    val weatherIcon: String = "",
    val temperatureLow: Double = 0.0,
    val temperatureHigh: Double = 0.0,
    val maxWindSpeed: Double = 0.0,
)
