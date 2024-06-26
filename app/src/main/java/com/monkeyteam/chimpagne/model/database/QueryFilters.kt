package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import java.util.Calendar

/** Filters used by the feed and the Discover Events page. */
fun containsTagsFilter(tags: List<String>): Filter {
  return Filter.arrayContainsAny("tags", tags)
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
  return Filter.equalTo("public", true)
}

fun happensInDateRangeFilter(startDate: Calendar, endDate: Calendar): Filter {
  val startTimestampValidity =
      buildTimestamp(
          startDate.get(Calendar.DATE),
          startDate.get(Calendar.MONTH),
          startDate.get(Calendar.YEAR),
          0,
          0)

  val endTimestampValidity =
      buildTimestamp(
          endDate.get(Calendar.DATE),
          endDate.get(Calendar.MONTH),
          endDate.get(Calendar.YEAR),
          23,
          59)

  return Filter.or(
      Filter.and(
          startsBeforeFilter(startTimestampValidity), endsAfterFilter(startTimestampValidity)),
      Filter.and(startsAfterFilter(startTimestampValidity), endsBeforeFilter(endTimestampValidity)),
      Filter.and(startsBeforeFilter(endTimestampValidity), endsAfterFilter(endTimestampValidity)))
}
