package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import java.util.Calendar

fun containsTagsFilter(tags: List<String>): Filter {
  return Filter.inArray("tags", tags)
}

fun startsBeforeFilter(timestamp: Timestamp): Filter {
  return Filter.lessThan("startsAtTimestamp", timestamp)
}

fun startsAfterFilter(timestamp: Timestamp): Filter {
  return Filter.greaterThan("startsAtTimestamp", timestamp)
}

fun endsBeforeFilter(timestamp: Timestamp): Filter {
  return Filter.lessThan("endsAtTimestamp", timestamp)
}

fun endsAfterFilter(timestamp: Timestamp): Filter {
  return Filter.greaterThan("endsAtTimestamp", timestamp)
}

fun onlyPublicFilter(): Filter {
  return Filter.equalTo("isPublic", true)
}

fun happensOnThisDateFilter(calendar: Calendar): Filter {
  val startDateCalendar = Calendar.getInstance()
  startDateCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
  startDateCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
  startDateCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
  startDateCalendar.set(Calendar.HOUR, 0)
  startDateCalendar.set(Calendar.MINUTE, 0)
  startDateCalendar.set(Calendar.SECOND, 0)

  val startTimestampValidity = Timestamp(startDateCalendar.time)
  val endDateCalendar = Calendar.getInstance()
  startDateCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
  startDateCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
  startDateCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
  startDateCalendar.set(Calendar.HOUR, 23)
  startDateCalendar.set(Calendar.MINUTE, 59)
  startDateCalendar.set(Calendar.SECOND, 59)

  val endTimestampValidity = Timestamp(endDateCalendar.time)
  return Filter.or(
      Filter.and(
          startsBeforeFilter(startTimestampValidity), endsAfterFilter(startTimestampValidity)),
      Filter.and(startsAfterFilter(startTimestampValidity), endsBeforeFilter(endTimestampValidity)),
      Filter.and(startsBeforeFilter(endTimestampValidity), endsAfterFilter(endTimestampValidity)))
}
