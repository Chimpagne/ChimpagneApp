package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCreationScreen(
    initialPage: Int = 0,
    navObject: NavigationActions,
    eventViewModel: EventViewModel = viewModel()
) {
  // This screen is made of several panels
  // The user can go from panel either by swiping left and right,
  // or by clicking the buttons on the bottom of the screen.

  val uiState by eventViewModel.uiState.collectAsState()

  val pagerState = rememberPagerState(initialPage = initialPage) { 4 }
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  Column {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
      GoBackButton(navigationActions = navObject)
    }
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
      when (page) {
        0 -> FirstPanel(eventViewModel)
        1 -> SecondPanel(eventViewModel)
        2 -> ThirdPanel(eventViewModel)
        3 -> FourthPanel(eventViewModel)
      }
    }

    // The logic below is to make sure to display the proper panel navigation button
    // throughout the panels
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      if (pagerState.currentPage > 0) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            }) {
              Text(stringResource(id = R.string.event_creation_screen_previous))
            }
      } else {
        Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))
      }
      if (pagerState.currentPage < 3) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }) {
              Text(stringResource(id = R.string.event_creation_screen_next))
            }
      } else {
        Button(
            onClick = {
              if (!uiState.loading) {
                Toast.makeText(context, "Creating the event...", Toast.LENGTH_SHORT).show()
                eventViewModel.createTheEvent(
                    onSuccess = {
                      Toast.makeText(context, "Event has been created !", Toast.LENGTH_SHORT).show()
                      navObject.goBack()
                    })
              }
            }) {
              Text(stringResource(id = R.string.event_creation_screen_create_event))
            }
      }
    }
  }
}

@Composable
fun FirstPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  var addressText by remember { mutableStateOf("") }

  Column(modifier = Modifier.padding(16.dp)) {
    OutlinedTextField(
        value = uiState.title,
        onValueChange = eventViewModel::updateEventTitle,
        label = { Text(stringResource(id = R.string.event_creation_screen_title)) },
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.description,
        onValueChange = eventViewModel::updateEventDescription,
        label = { Text(stringResource(id = R.string.event_creation_screen_description)) },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = addressText,
        onValueChange = { addressText = it },
        label = { Text(stringResource(id = R.string.event_creation_screen_address)) },
        modifier = Modifier.fillMaxWidth())
  }
}

@Composable
fun SecondPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  var tagsText by remember { mutableStateOf("") }

  val context = LocalContext.current
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_more_event_infos),
        style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.tags.joinToString(","),
        onValueChange = { eventViewModel.updateEventTags(it.split(",")) },
        label = { Text(stringResource(id = R.string.event_creation_screen_tags)) },
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
          checked = uiState.isPublic,
          onCheckedChange = { eventViewModel.updateEventPublicity(!uiState.isPublic) })
      Text(stringResource(id = R.string.event_creation_screen_make_event_public))
    }
  }
}

@Composable
fun ThirdPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_groceries),
        style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { /* Add groceries logic */}) {
      Text(stringResource(id = R.string.event_creation_screen_add_groceries))
    }
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn {
      // Populate with groceries items
    }
  }
}

// Comment to make a new commit
@Composable
fun FourthPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  // TODO: SHOW SPINNER

  var parkingText by remember { mutableStateOf("") }
  var bedsText by remember { mutableStateOf("") }
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_logistics),
        style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(id = R.string.event_creation_screen_parking),
        style = MaterialTheme.typography.bodyMedium)

    OutlinedTextField(
        value = parkingText,
        onValueChange = { parkingText = it },
        label = { Text(stringResource(id = R.string.event_creation_screen_number_parking)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth())

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(id = R.string.event_creation_screen_beds),
        style = MaterialTheme.typography.bodyMedium)
    OutlinedTextField(
        value = bedsText,
        onValueChange = { bedsText = it },
        label = { Text(stringResource(id = R.string.event_creation_screen_number_beds)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth())
  }
}
