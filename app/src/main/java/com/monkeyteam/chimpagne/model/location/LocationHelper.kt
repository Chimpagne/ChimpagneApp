package com.monkeyteam.chimpagne.model.location

import android.util.Log
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray

class LocationHelper {

  private val _markers = MutableStateFlow<List<Location>>(emptyList())
  val markers: StateFlow<List<Location>> = _markers.asStateFlow()

  suspend fun convertNameToLocation(name: String, onResult: (Location) -> Unit) {
    val client = OkHttpClient()
    val urlSearchNominatim = "https://nominatim.openstreetmap.org/search?q=$name&format=json"
    val request = okhttp3.Request.Builder().url(urlSearchNominatim).build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {

              override fun onFailure(call: Call, e: IOException) {
                Log.e(
                    "LocationHelper",
                    "Failed to get location for $name, because of IO Exception",
                    e)
                onResult(Location(name))
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                    Log.e(
                        "LocationHelper",
                        "Failed to get location for $name, because of unsuccessful response")
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
                      Log.e(
                          "LocationHelper",
                          "Failed to get location for $name, because of empty response")
                      onResult(Location(name))
                    }
                  }
                }
              }
            })
  }

  suspend fun addMarker(location: Location) {
    val currentMarkers = _markers.value.toMutableList()
    currentMarkers.add(location)
    _markers.value = currentMarkers
  }
}
