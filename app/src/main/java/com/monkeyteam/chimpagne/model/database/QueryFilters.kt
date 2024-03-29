package com.monkeyteam.chimpagne.model.database

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter

fun containsInTitle(title: String): Filter {
  return Filter.arrayContains("title", title)
}

fun containsInDescription(description: String): Filter {
  return Filter.arrayContains("description", description)
}

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

fun onlyPublic(): Filter {
  return Filter.equalTo("isPublic", true)
}
