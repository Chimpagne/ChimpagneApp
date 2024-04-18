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
    var SCHEME = "https"
    var HOST = "nominatim.openstreetmap.org"
    var PORT = 443
}

fun convertNameToLocations(name: String, onResult: (List<Location>) -> Unit, limit: Int = 5) {
    val client = OkHttpClient()

    val url = HttpUrl.Builder()
        .scheme(NominatimConstants.SCHEME)
        .host(NominatimConstants.HOST)
        .port(NominatimConstants.PORT)
        .addPathSegment("search.php")
        .addQueryParameter("q", java.net.URLEncoder.encode(name, "UTF-8"))
        .addQueryParameter("format", "jsonv2")
        .addQueryParameter("addressdetails", "1")
        .addQueryParameter("limit", limit.toString())
        .build()

    val request = Request.Builder().url(url).addHeader("User-Agent", "Chimpagne").build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("LocationHelper", "Failed to get location for $name, because of IO Exception", e)
            onResult(listOf())
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    Log.e("LocationHelper", "Failed to get location for $name, because of unsuccessful response")
                    onResult(listOf())
                } else {
                    val jsonString = response.body?.string()
                    val jsonArray = jsonString?.let { JSONArray(it) }
                    val uniqueDisplayNames = mutableSetOf<String>()
                    val locations = mutableListOf<Location>()

                    if (jsonArray != null) {
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val addressObject = jsonObject.getJSONObject("address")
                            val category = jsonObject.optString("category", "")
                            val country = addressObject.optString("country", "")
                            val normalizedCountry = if (country == "Schweiz/Suisse/Svizzera/Svizra") "Switzerland" else country

                            val displayName = when {
                                category == "building" -> {
                                    val road = addressObject.optString("road", "")
                                    val houseNumber = addressObject.optString("house_number", "")
                                    val postcode = addressObject.optString("postcode", "")
                                    val village = addressObject.optString("village", addressObject.optString("town", addressObject.optString("city", "")))
                                    "$road $houseNumber, $postcode, $village, $normalizedCountry"
                                }
                                category == "amenity" -> {
                                    val amenity = addressObject.optString("amenity", "")
                                    val locality = addressObject.optString("city", addressObject.optString("town", addressObject.optString("village", "")))
                                    val postcode = addressObject.optString("postcode", "")
                                    "$amenity, $locality, $postcode, $normalizedCountry"
                                }
                                else -> {
                                    val specificCategoryValue = addressObject.optString(category, "")
                                    val locality = addressObject.optString("city", addressObject.optString("town", addressObject.optString("village", "")))
                                    val postcode = addressObject.optString("postcode", "")
                                    if (specificCategoryValue.isNotEmpty()) "$specificCategoryValue, $category, $locality, $postcode, $normalizedCountry"
                                    else "$locality, $postcode, $normalizedCountry"
                                }
                            }

                            // Deduplication based on display name
                            if (uniqueDisplayNames.add(displayName)) {
                                val lat = jsonObject.getDouble("lat")
                                val lon = jsonObject.getDouble("lon")
                                locations.add(Location(displayName, lat, lon))
                            }
                        }
                    }

                    if (locations.isNotEmpty()) {
                        onResult(locations)
                    } else {
                        Log.e("LocationHelper", "No unique locations found for $name")
                        onResult(listOf())
                    }
                }
            }
        }
    })
}
