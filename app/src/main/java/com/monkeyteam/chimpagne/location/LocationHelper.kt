package com.monkeyteam.chimpagne.model.location

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class LocationHelper {

    suspend fun convertNameToLocation(name: String, onResult: (Location) -> Unit) {
        val client = OkHttpClient()
        val urlSearchNominatim = "https://nominatim.openstreetmap.org/search?q=$name&format=json"
        val request = okhttp3.Request.Builder().url(urlSearchNominatim).build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // Give error feedback?
                onResult(Location(name))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Give error feedback?
                        onResult(Location(name))
                    } else {
                        val geoLocation = response.body?.string()
                        val jsonArray = geoLocation?.let { JSONArray(it) }
                        if (jsonArray != null && jsonArray.length() > 0) {
                            val jsonObject = jsonArray.getJSONObject(0)
                            val lat = jsonObject.getDouble("lat")
                            val lon = jsonObject.getDouble("lon")
                            val locationInfo = Location(name, lat, lon)
                            onResult(locationInfo)
                        } else {
                            onResult(Location(name))
                        }
                    }
                }
            }
        }
        )
    }


}