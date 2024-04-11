package com.monkeyteam.chimpagne.model.location

import android.util.Log
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

object NominatimConstants {
  const val SCHEME = "https"
  const val HOST = "nominatim.openstreetmap.org"
}

fun convertNameToLocations(name: String, onResult: (List<Location>) -> Unit, limit: Int = 5) {

  val client = OkHttpClient()

  val url =
      HttpUrl.Builder()
          .scheme(NominatimConstants.SCHEME)
          .host(NominatimConstants.HOST)
          .addPathSegment("search.php")
          .addQueryParameter("q", java.net.URLEncoder.encode(name, "UTF-8"))
          .addQueryParameter("format", "jsonv2")
          .addQueryParameter("limit", limit.toString())
          .build()

  val request = Request.Builder().url(url).addHeader("User-Agent", "Chimpagne").build()
  client
      .newCall(request)
      .enqueue(
          object : Callback {

            override fun onFailure(call: Call, e: IOException) {
              Log.e(
                  "LocationHelper", "Failed to get location for $name, because of IO Exception", e)
              onResult(listOf())
            }

            override fun onResponse(call: Call, response: Response) {
              response.use {
                if (!response.isSuccessful) {
                  Log.e(
                      "LocationHelper",
                      "Failed to get location for $name, because of unsuccessful response")
                  onResult(listOf())
                } else {
                  val geoLocation = response.body?.string()
                  val jsonArray = geoLocation?.let { JSONArray(it) }
                  if (jsonArray != null && jsonArray.length() > 0) {
                    val locations = arrayListOf<Location>()
                    for (i in 0 ..< jsonArray.length()) {
                      val jsonObject = jsonArray.getJSONObject(i)
                      val locName = jsonObject.getString("display_name")
                      val lat = jsonObject.getDouble("lat")
                      val lon = jsonObject.getDouble("lon")
                      locations.add(Location(locName, lat, lon))
                    }
                    onResult(locations)
                  } else {
                    Log.e(
                        "LocationHelper",
                        "Failed to get location for $name, because of empty response")
                    onResult(listOf())
                  }
                }
              }
            }
          })
}
