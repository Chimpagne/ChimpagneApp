package com.monkeyteam.chimpagne.ui.event

import DateSelector
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  Column(modifier = Modifier.padding(16.dp)) {
    Legend( //Placing it first, makes it easier for the user to click on a suggestion
        stringResource(id = R.string.event_creation_screen_location_legend),
        Icons.Rounded.LocationOn,
        "Location")

    Spacer(modifier = Modifier.height(16.dp))

    LocationSelector(uiState.location, eventViewModel::updateEventLocation)

    Spacer(Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_title_legend),
        Icons.Rounded.Title,
        "Title")
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.title,
        onValueChange = eventViewModel::updateEventTitle,
        label = { Text(stringResource(id = R.string.event_creation_screen_title)) },
        modifier = Modifier.fillMaxWidth().testTag("add_a_title"))

    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_description_legend),
        Icons.Rounded.Description,
        "Description")

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = uiState.description,
        onValueChange = eventViewModel::updateEventDescription,
        label = { Text(stringResource(id = R.string.event_creation_screen_description)) },
        modifier = Modifier.fillMaxWidth().testTag("add_a_description"),
        maxLines = 3)

    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_start_date_legend),
        Icons.Rounded.CalendarToday,
        "Start Date")

    Spacer(modifier = Modifier.height(16.dp))

    DateSelector(
        selectedDate = uiState.startsAtCalendarDate,
        onDateSelected = eventViewModel::updateEventStartCalendarDate,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        selectTimeOfDay = true)

    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_end_date_legend),
        Icons.Rounded.CalendarToday,
        "End Date")

    Spacer(modifier = Modifier.height(16.dp))
    // We will need to add some tests for DateSelector also
    DateSelector(
        selectedDate = uiState.endsAtCalendarDate,
        onDateSelected = eventViewModel::updateEventEndCalendarDate,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        selectTimeOfDay = true)
  }
}
