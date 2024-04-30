package com.monkeyteam.chimpagne.model.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.firebase.Timestamp
import com.monkeyteam.chimpagne.R
import java.text.DateFormat
import java.util.Calendar

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
  return calendar
}

fun buildTimestamp(day: Int, month: Int, year: Int, hour: Int, minute: Int): Timestamp {
  return buildTimestamp(buildCalendar(day, month, year, hour, minute))
}

@Composable
fun timestampToStringWithDateAndTime(timestamp: Timestamp): String {
  return DateFormat.getDateInstance(DateFormat.LONG).format(timestamp.toDate()) +
      " " +
      stringResource(id = R.string.date_tools_at) +
      " " +
      DateFormat.getTimeInstance(DateFormat.SHORT).format(timestamp.toDate())
}
