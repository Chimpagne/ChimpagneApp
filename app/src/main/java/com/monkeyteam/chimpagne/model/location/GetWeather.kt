package com.monkeyteam.chimpagne.model.location

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate

fun getWeather(
    location: Location,
    date: LocalDate,
    onSuccess: (Weather) -> Unit,
    onFailure: () -> Unit,
    apiKey: String
) {
  val okHttpClient = OkHttpClient()

  val freeWeatherApiUrl =
      HttpUrl.Builder()
          .scheme("https")
          .host("api.weatherapi.com")
          .addPathSegment("v1")
          .addPathSegment("forecast.json")
          .addQueryParameter("key", apiKey)
          .addQueryParameter("q", "${location.latitude},${location.longitude}")
          .addQueryParameter("dt", date.toString())
          .build()
  Log.d("WeatherHelper", "Requesting weather for $location at $date from $freeWeatherApiUrl")

  val request =
      Request.Builder().url(freeWeatherApiUrl).addHeader("User-Agent", "Chimpagne").build()
  okHttpClient
      .newCall(request)
      .enqueue(
          object : Callback {
            override fun onFailure(call: Call, e: IOException) {
              Log.e(
                  "WeatherHelper",
                  "Failed to get weather for $location, because of IO Exception",
                  e)
              onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
              response.use {
                if (!response.isSuccessful) {
                  Log.e(
                      "WeatherHelper",
                      "Failed to get weather for $location, because of unsuccessful response")
                  onFailure()
                } else {
                  val jsonString = response.body?.string()
                  if (jsonString.isNullOrEmpty()) {
                    Log.e("WeatherHelper", "Invalid JSON response")
                    onFailure()
                  } else {
                    try {
                      val weatherResponse = JSONObject(jsonString)
                      if (weatherResponse.getJSONObject("location").getDouble("lat") ==
                          location.latitude &&
                          weatherResponse.getJSONObject("location").getDouble("lon") ==
                              location.longitude) {
                        val forecastDay =
                            weatherResponse
                                .getJSONObject("forecast")
                                .getJSONArray("forecastday")
                                .getJSONObject(0)

                        val weather =
                            Weather(
                                location = location,
                                weatherDescription =
                                forecastDay
                                    .getJSONObject("day")
                                    .getJSONObject("condition")
                                    .getString("text"),
                                weatherIcon =
                                forecastDay
                                    .getJSONObject("day")
                                    .getJSONObject("condition")
                                    .getString("icon"),
                                temperatureLow =
                                    forecastDay.getJSONObject("day").getDouble("mintemp_c"),
                                temperatureHigh =
                                    forecastDay.getJSONObject("day").getDouble("maxtemp_c"),
                                maxWindSpeed =
                                    forecastDay.getJSONObject("day").getDouble("maxwind_kph"),
                                windDirection = forecastDay.getJSONObject("day").getString("wind_dir"))

                        onSuccess(weather)
                      } else {
                        Log.e("WeatherHelper", "Json response does not match location")
                        onFailure()
                      }
                    } catch (e: Exception) {
                      Log.e("WeatherHelper", "Invalid JSON response", e)
                      onFailure()
                    }
                  }
                }
              }
            }
          })
}
