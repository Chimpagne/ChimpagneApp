package com.monkeyteam.chimpagne.model.location

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

data class Location(
    val name: String, val latitude: Double, val longitude: Double, val geohash: String
) {
    constructor() : this(
        name = "", latitude = 0.0, longitude = 0.0, geohash = ""
    )

    constructor(
        name: String
    ) : this(
        name = name, latitude = 0.0, longitude = 0.0, geohash = ""
    )

    constructor(name: String, latitude: Double, longitude: Double) : this(
        name,
        latitude,
        longitude,
        GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
    )
}
