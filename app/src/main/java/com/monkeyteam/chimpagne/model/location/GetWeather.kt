package com.monkeyteam.chimpagne.model.location

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.time.LocalDate
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

object GetWeatherConstants {
  var PORT = 443
  var SCHEME = "https"
  var HOST = "api.weatherapi.com"
  var VERSION = "v1"
  var FORECAST = "forecast.json"
}

fun getWeather(
    location: Location,
    date: LocalDate,
    onSuccess: (Weather) -> Unit,
    onFailure: () -> Unit,
    context: Context
) {
  val apiKey =
      try {
        val applicationInfo =
            context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA)
        applicationInfo.metaData.getString(
            "com.monkeyteam.chimpagne.model.location.WEATHER_API_KEY")
      } catch (e: Exception) {
        Log.e("WeatherHelper", "Failed to get API key", e)
        onFailure()
        return
      }

  val okHttpClient = OkHttpClient()

  val freeWeatherApiUrl =
      HttpUrl.Builder()
          .port(GetWeatherConstants.PORT)
          .scheme(GetWeatherConstants.SCHEME)
          .host(GetWeatherConstants.HOST)
          .addPathSegment(GetWeatherConstants.VERSION)
          .addPathSegment(GetWeatherConstants.FORECAST)
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
                  try {
                    val jsonString = response.body?.string()
                    val weatherResponse = JSONObject(jsonString!!)
                    val forecastDay =
                        weatherResponse
                            .getJSONObject("forecast")
                            .getJSONArray("forecastday")
                            .getJSONObject(0)

                    val weather =
                        Weather(
                            location = location,
                            date = date,
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
                            windDirection = forecastDay.getJSONObject("day").optInt("wind_deg", -1))

                    onSuccess(weather)
                  } catch (e: Exception) {
                    Log.e("WeatherHelper", "Invalid JSON response", e)
                    onFailure()
                  }
                }
              }
            }
          })
}
