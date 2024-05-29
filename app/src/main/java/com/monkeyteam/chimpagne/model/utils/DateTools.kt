package com.monkeyteam.chimpagne.model.utils

import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun buildTimestamp(calendar: Calendar): Timestamp {
  return Timestamp(calendar.time)
}

fun buildCalendar(timestamp: Timestamp): Calendar {
  val calendar = Calendar.getInstance()
  calendar.time = timestamp.toDate()
  return calendar
}

fun buildCalendar(day: Int, month: Int, year: Int, hour: Int, minute: Int): Calendar {
  val calendar = Calendar.getInstance()
  calendar.set(year, month, day, hour, minute, 0)
  calendar.set(Calendar.MILLISECOND, 0)
  return calendar
}

fun buildTimestamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Timestamp {
  return buildTimestamp(buildCalendar(day, month, year, hour, minute))
}

fun setCalendarToMidnight(calendar: Calendar) {
  calendar.set(Calendar.HOUR_OF_DAY, 0)
  calendar.set(Calendar.MINUTE, 0)
  calendar.set(Calendar.SECOND, 0)
  calendar.set(Calendar.MILLISECOND, 0)
}

@Composable
fun timestampToStringWithDateAndTime(timestamp: Timestamp): String {
  return DateFormat.getDateInstance(DateFormat.LONG).format(timestamp.toDate()) +
      " " +
      stringResource(id = R.string.date_tools_at) +
      " " +
      DateFormat.getTimeInstance(DateFormat.SHORT).format(timestamp.toDate())
}

/** Returns the date in dd/MM/yyyy format. */
@Composable
fun simpleDateFormat(timestamp: Timestamp): String {
  val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
  return dateFormat.format(timestamp.toDate())
}

/** Returns the time in HH:mm format. */
@Composable
fun simpleTimeFormat(timestamp: Timestamp): String {
  val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  return timeFormat.format(timestamp.toDate())
}

fun createCalendarIntent(event: ChimpagneEvent?): Intent? {
  if (event == null) {
    Log.e("CalendarIntents", "Event is null")
    return null
  }

  val intent =
      Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, event.title)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startsAt().timeInMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endsAt().timeInMillis)
        putExtra(CalendarContract.Events.EVENT_LOCATION, event.location.asGooglePlex())
        putExtra(CalendarContract.Reminders.MINUTES, 1440) // 1 day before
        putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
      }
  Log.d("CalendarIntents", "Calendar Intent created successfully")
  return intent
}
