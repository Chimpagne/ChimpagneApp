package com.monkeyteam.chimpagne.model.location

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val geohash: String
) {
  constructor() : this(name = "", latitude = 0.0, longitude = 0.0, geohash = "")

  constructor(name: String) : this(name = name, latitude = 0.0, longitude = 0.0, geohash = "")

  fun asGooglePlex(): String {
    return "$latitude,$longitude"
  }

  constructor(
      name: String,
      latitude: Double,
      longitude: Double
  ) : this(
      name,
      latitude,
      longitude,
      GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude)))
}
