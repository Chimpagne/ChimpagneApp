package com.monkeyteam.chimpagne.model.location

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation

data class Location(
        val name: String,
        var latitude: Double,
        var longitude: Double,
        val geohash: String
) {
  constructor() : this(name = "", latitude = 0.0, longitude = 0.0, geohash = "")

  constructor(name: String) : this(name = name, latitude = 0.0, longitude = 0.0, geohash = "")

  constructor(
      name: String,
      latitude: Double,
      longitude: Double
  ) : this(
      name,
      latitude,
      longitude,
      GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude)))

    /*
     It applies the Haversine formula to calculate the great-circle distance between the two locations:
   - It calculates the square of the half-chord length between the two points (`a`) using the Haversine formula.
   - It calculates the angular distance in radians (`c`) using the inverse Haversine formula.

4. Finally, it multiplies the angular distance (`c`) by the Earth's radius (`earthRadius`) to obtain the distance in kilometers.
     */
    fun distanceTo(other: Location): Double {
        val earthRadius = 6371.0 // in kilometers

        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(latitude)) * kotlin.math.cos(Math.toRadians(other.latitude)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadius * c
    }

}
