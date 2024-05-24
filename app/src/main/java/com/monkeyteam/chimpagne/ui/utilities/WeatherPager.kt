package com.monkeyteam.chimpagne.ui.utilities

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Weather
import com.monkeyteam.chimpagne.model.location.getWeather
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@Composable
fun WeatherPager(event: ChimpagneEvent) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var weatherData by remember { mutableStateOf<List<Weather>?>(null) }
  var message by remember { mutableStateOf<String?>(null) }

  val today = LocalDate.now()
  val eventStartDate = event.startsAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
  val eventEndDate = event.endsAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

  val targetStartDate = maxOf(today, eventStartDate)

  LaunchedEffect(event) {
    if (eventEndDate.isBefore(today)) {
      message = context.getString(R.string.noforecast_alreadyoccured)
    } else if (targetStartDate.isAfter(today.plusDays(10))) {
      message = context.getString(R.string.noforecast_eventtoofarinfuture)
    } else {
      coroutineScope.launch {
        val weatherList = mutableListOf<Weather>()
        val dateRange = generateDateRange(targetStartDate, minOf(eventEndDate, today.plusDays(10)))
        dateRange.forEach { date ->
          getWeather(
              event.location,
              date,
              { weather ->
                weatherList.add(weather)
                if (weatherList.size == dateRange.size) {
                  weatherList.sortBy { it.date }
                  weatherData = weatherList.toList()
                  Log.d("WeatherApp", "Weather data: $weatherList")
                }
              },
              {
                // Handle failure
                message = context.getString(R.string.noforecast_error)
              },
              context)
        }
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    when {
      message != null -> TextMessage(message)
      weatherData != null -> WeatherCarouselInternal(weatherData!!)
      else -> CircularProgressIndicator(modifier = Modifier.testTag("loading_indicator"))
    }
  }
}

fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
  val dates = mutableListOf<LocalDate>()
  var current = start
  while (current <= end) {
    dates.add(current)
    current = current.plusDays(1)
  }
  return dates
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherCarouselInternal(weatherData: List<Weather>) {
  val pagerState = rememberPagerState { weatherData.size }

  Column {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        beyondBoundsPageCount = weatherData.size - 1) { page ->
          WeatherCard(weatherData[page])
        }

    Row(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)) {
      repeat(weatherData.size) { iteration ->
        val color =
            if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
            else Color.LightGray
        Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(10.dp))
      }
    }
  }
}

@Composable
fun WeatherCard(weather: Weather) {
  val configuration = LocalConfiguration.current
  val locale = configuration.locales[0]
  val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", locale)
  val formattedDate = weather.date.format(formatter)

  Card(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).testTag("weather card"),
      shape = MaterialTheme.shapes.medium) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                  text = formattedDate,
                  style = ChimpagneTypography.titleSmall.copy(fontWeight = FontWeight.Bold))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape)) {
                      Image(
                          painter = rememberAsyncImagePainter("https:" + weather.weatherIcon),
                          contentDescription = null,
                          modifier = Modifier.size(56.dp))
                    }
                    Column {
                      Text(
                          modifier = Modifier.testTag("weather temp"),
                          text =
                              "Temp : ${weather.temperatureLow}°C -> ${weather.temperatureHigh}°C",
                          style = ChimpagneTypography.bodyMedium)
                      Text(
                          modifier = Modifier.testTag("weather wind"),
                          text =
                              stringResource(R.string.wind_speed) +
                                  " : ${weather.maxWindSpeed} km/h",
                          style = ChimpagneTypography.bodyMedium)
                    }
                  }
            }
      }
}

@Composable
fun TextMessage(message: String?) {
  if (message != null) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("weather message"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text(text = message, style = ChimpagneTypography.bodyMedium)
        }
  }
}
