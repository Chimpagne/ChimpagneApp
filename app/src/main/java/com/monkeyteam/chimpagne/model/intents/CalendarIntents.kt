package com.monkeyteam.chimpagne.model.intents

import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent

class CalendarIntents {
  fun addToCalendar(event: ChimpagneEvent?): Intent? {
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
}
