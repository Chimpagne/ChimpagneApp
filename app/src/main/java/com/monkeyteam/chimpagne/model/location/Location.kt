package com.monkeyteam.chimpagne.model.location

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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

    companion object {
        fun convertNameToLocation(
            name: String, onResult: (List<Location>) -> Unit, limit: Int = 5
        ) {

            val client = OkHttpClient()

            val url =
                HttpUrl.Builder()
                    .scheme("https")
                    .host("nominatim.openstreetmap.org")
                    .addPathSegment("search")
                    .addQueryParameter("q", java.net.URLEncoder.encode(name, "UTF-8"))
                    .addQueryParameter("format", "json")
                    .addQueryParameter("limit", limit.toString())
                    .build()

            val request = Request.Builder().url(url).addHeader("User-Agent", "Chimpagne").build()
            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    Log.e(
                        "LocationHelper",
                        "Failed to get location for $name, because of IO Exception",
                        e
                    )
                    onResult(listOf())
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.e(
                                "LocationHelper",
                                "Failed to get location for $name, because of unsuccessful response"
                            )
                            onResult(listOf())
                        } else {
                            val geoLocation = response.body?.string()
                            val jsonArray = geoLocation?.let { JSONArray(it) }
                            if (jsonArray != null && jsonArray.length() > 0) {
                                val locations = arrayListOf<Location>()
                                for (i in 0..<jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val lat = jsonObject.getDouble("lat")
                                    val lon = jsonObject.getDouble("lon")
                                    locations.add(Location(name, lat, lon))
                                }
                                onResult(locations)
                            } else {
                                Log.e(
                                    "LocationHelper",
                                    "Failed to get location for $name, because of empty response"
                                )
                                onResult(listOf())
                            }
                        }
                    }
                }
            })
        }
    }
}
