package com.monkeyteam.chimpagne.model.utils

import com.google.firebase.Timestamp
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

fun timestampToStringWithDateAndTime(timestamp: Timestamp): String{
  return DateFormat.getDateInstance(DateFormat.LONG)
            .format(timestamp.toDate()) +
          " at " +
          DateFormat.getTimeInstance(DateFormat.SHORT)
            .format(timestamp.toDate())
}
