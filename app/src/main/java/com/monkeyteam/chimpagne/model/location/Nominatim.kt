package com.monkeyteam.chimpagne.model.location

import android.util.Log
import java.io.IOException
import java.util.Locale
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

fun getCurrentLanguage(): String {
  return Locale.getDefault().language
}

fun convertNameToLocations(name: String, onResult: (List<Location>) -> Unit, limit: Int = 5) {
  val client = OkHttpClient()

  val url =
      HttpUrl.Builder()
          .scheme(NominatimConstants.SCHEME)
          .host(NominatimConstants.HOST)
          .port(NominatimConstants.PORT)
          .addPathSegment("search.php")
          .addQueryParameter("q", java.net.URLEncoder.encode(name, "UTF-8"))
          .addQueryParameter("format", "jsonv2")
          .addQueryParameter("addressdetails", "1")
          .addQueryParameter("limit", limit.toString())
          .build()

  val language = getCurrentLanguage()
  val acceptLanguage = if (language == "fr") "fr" else "en"

  val request =
      Request.Builder()
          .url(url)
          .addHeader("User-Agent", "Chimpagne")
          .addHeader("Accept-Language", acceptLanguage)
          .build()

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
                  val jsonString = response.body?.string()
                  if (jsonString.isNullOrEmpty() || !jsonString.startsWith("[")) {
                    Log.e("LocationHelper", "Invalid JSON response")
                    onResult(listOf())
                    return
                  }
                  val jsonArray = JSONArray(jsonString)
                  val uniqueDisplayNames = mutableSetOf<String>()
                  val locations = mutableListOf<Location>()

                  for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val addressObject = jsonObject.optJSONObject("address") ?: continue
                    val category = jsonObject.optString("category", "")
                    val country = addressObject.optString("country", "")
                    val normalizedCountry =
                        if (country == "Schweiz/Suisse/Svizzera/Svizra") "Switzerland" else country

                    val displayName =
                        when (category) {
                          "building" -> {
                            val components =
                                listOfNotNull(
                                    addressObject.optString("road", "").takeIf { it.isNotEmpty() },
                                    addressObject.optString("house_number", "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    addressObject.optString("postcode", "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    addressObject
                                        .optString(
                                            "village",
                                            addressObject.optString(
                                                "town", addressObject.optString("city", "")))
                                        .takeIf { it.isNotEmpty() },
                                    normalizedCountry.takeIf { it.isNotEmpty() })

                            components.joinToString(", ")
                          }
                          "amenity" -> {
                            val components =
                                listOfNotNull(
                                    addressObject.optString("amenity", "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    addressObject
                                        .optString(
                                            "city",
                                            addressObject.optString(
                                                "town", addressObject.optString("village", "")))
                                        .takeIf { it.isNotEmpty() },
                                    addressObject.optString("postcode", "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    normalizedCountry.takeIf { it.isNotEmpty() })

                            components.joinToString(", ")
                          }
                          else -> {
                            val components =
                                listOfNotNull(
                                    addressObject.optString(category, "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    addressObject
                                        .optString(
                                            "city",
                                            addressObject.optString(
                                                "town", addressObject.optString("village", "")))
                                        .takeIf { it.isNotEmpty() },
                                    addressObject.optString("postcode", "").takeIf {
                                      it.isNotEmpty()
                                    },
                                    normalizedCountry.takeIf { it.isNotEmpty() })

                            components.joinToString(", ")
                          }
                        }

                    if (uniqueDisplayNames.add(displayName)) {
                      val lat = jsonObject.getDouble("lat")
                      val lon = jsonObject.getDouble("lon")
                      locations.add(Location(displayName, lat, lon))
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
