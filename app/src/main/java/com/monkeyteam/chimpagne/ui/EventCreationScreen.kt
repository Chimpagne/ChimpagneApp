package com.monkeyteam.chimpagne.ui

import DateSelector
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneSupply
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.components.TagField
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyPopup(
    onDismissRequest: () -> Unit,
    onSave: (String, Int, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }

    Dialog(
        content = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                TextField(
                    maxLines = 1,
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.supplies_description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supplies_description_field")
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(

                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text(stringResource(id = R.string.supplies_quantity)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { /* Handle Done action */ }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supplies_quantity_field")
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    maxLines = 1,
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text(stringResource(id = R.string.supplies_unit)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("supplies_unit_field")
                )
                Row {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        modifier = Modifier.testTag("supplies_cancel_button")
                    ) {
                        Text(stringResource(id = R.string.chimpagne_cancel))
                    }

                    Button(
                        onClick = {
                            onSave(description, quantity.toIntOrNull() ?: 0, unit)
                            onDismissRequest()
                        },
                        modifier = Modifier.testTag("supplies_add_button")
                    ) {
                        Text(stringResource(id = R.string.chimpagne_add))
                    }
                }
            }
        },

        onDismissRequest = {
            onDismissRequest()
        }
    )
}

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
    val suppliesRememberList = remember { mutableStateListOf<ChimpagneSupply>() }

  val context = LocalContext.current
  Column {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
      GoBackButton(navigationActions = navObject)
      Text(text = stringResource(id = R.string.event_creation_screen_name))
    }
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
      when (page) {
        0 -> FirstPanel(eventViewModel)
        1 -> SecondPanel(eventViewModel)
        2 -> ThirdPanel(eventViewModel, suppliesRememberList)
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
            },
            modifier = Modifier.testTag("previous_button")) {
              Text(stringResource(id = R.string.event_creation_screen_previous))
            }
      } else {
        Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))
      }
      if (pagerState.currentPage < 3) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            modifier = Modifier.testTag("next_button")) {
              Text(stringResource(id = R.string.event_creation_screen_next))
            }
      } else {
        Button(
            onClick = {
                eventViewModel.uiState.value.supplies = suppliesRememberList.map { s -> s.id }
              if (!uiState.loading) {
                Toast.makeText(
                        context,
                        context.getString(R.string.event_creation_screen_toast_creating),
                        Toast.LENGTH_SHORT)
                    .show()
                eventViewModel.createTheEvent(
                    onSuccess = {
                      Toast.makeText(
                              context,
                              context.getString(R.string.event_creation_screen_toast_finish),
                              Toast.LENGTH_SHORT)
                          .show()
                      navObject.goBack()
                    })
              }
            },
            modifier = Modifier.testTag("create_event_button")) {
              Text(stringResource(id = R.string.event_creation_screen_create_event))
            }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  Column(modifier = Modifier.padding(16.dp)) {
    Legend(
        stringResource(id = R.string.event_creation_screen_title_legend),
        Icons.Rounded.Title,
        "Title")
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
        isError = uiState.title.isNotEmpty(),
        value = uiState.title,
        onValueChange = eventViewModel::updateEventTitle,
        label = { Text(stringResource(id = R.string.event_creation_screen_title)) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_a_title"))

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
        modifier = Modifier
            .fillMaxWidth()
            .testTag("add_a_description"),
        maxLines = 3)
    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_location_legend),
        Icons.Rounded.LocationOn,
        "Location")

    Spacer(modifier = Modifier.height(16.dp))

    LocationSelector(uiState.location, eventViewModel::updateEventLocation)

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

@Composable
fun SecondPanel(eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()

  var tagFieldActive by remember { mutableStateOf(true) }

  Column(modifier = Modifier.padding(16.dp)) {
    Legend(
        stringResource(id = R.string.event_creation_screen_tags_legend), Icons.Rounded.Tag, "Tags")

    TagField(
        uiState.tags,
        eventViewModel::updateEventTags,
        { tagFieldActive = it },
        Modifier
            .fillMaxWidth()
            .testTag("tag_field"))

    Spacer(modifier = Modifier.height(16.dp))

    Legend(
        stringResource(id = R.string.event_creation_screen_public_legend),
        Icons.Rounded.Public,
        "Public")

    Spacer(modifier = Modifier.height(16.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
      Checkbox(
          checked = uiState.public,
          onCheckedChange = { eventViewModel.updateEventPublicity(!uiState.public) })
      if (uiState.public)
          Text(stringResource(id = R.string.event_creation_screen_event_made_public))
      else Text(stringResource(id = R.string.event_creation_screen_make_event_public))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPanel(eventViewModel: EventViewModel, suppliesRememberList: SnapshotStateList<ChimpagneSupply>) {
    val showAddDialog = remember {
        mutableStateOf(false)
    }
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_groceries),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("groceries_title"))
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
         showAddDialog.value = true
        },
        modifier = Modifier.testTag("add_groceries_button")) {

          Text(stringResource(id = R.string.event_creation_screen_add_groceries))
        }

      if(showAddDialog.value){

          SupplyPopup(
              onDismissRequest = { showAddDialog.value = false },
              onSave = {supplyDescription, supplyQuantity, supplyUnit ->
                  eventViewModel.registerSupply(
                      onSuccess={supply: ChimpagneSupply -> suppliesRememberList.add(supply) },
                      onFailure = {},
                      supply = ChimpagneSupply(description = supplyDescription, quantity = supplyQuantity, unit = supplyUnit))
              }
          )

      }

    Spacer(modifier = Modifier.height(16.dp))
      LazyColumn {
          items(suppliesRememberList) { item ->

              ListItem(headlineContent = { Text(
                  text = item.description,
              ) }, supportingContent = {
                  Text(
                      text = item.quantity.toString() + " "+item.unit,
                      color = Color.Gray
                  )
              }, trailingContent = {
                  IconButton(
                      onClick = {eventViewModel.deleteSupply(item.id, onSuccess = {
                          suppliesRememberList.remove(item)
                      }) },
                      modifier = Modifier.testTag(item.description),
                      content = {
                          Icon(active = true, activeContent = { androidx.compose.material3.Icon(
                              imageVector = Icons.Default.Cancel,
                              contentDescription = "Delete"
                          )}, inactiveContent = { androidx.compose.material3.Icon(
                              imageVector = Icons.Default.Cancel,
                              contentDescription = "Delete"
                          )} )
                      }
                  )
              })
          }
      }
  }
}

@Composable
fun FourthPanel(eventViewModel: EventViewModel) {

  var parkingText by remember { mutableStateOf("") }
  var bedsText by remember { mutableStateOf("") }
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_logistics),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("logistics_title"))
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(id = R.string.event_creation_screen_parking),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag("parking_title"))
    OutlinedTextField(
        value = parkingText,
        onValueChange = { parkingText = it },
        label = { Text(stringResource(id = R.string.event_creation_screen_number_parking)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("n_parking"))

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        stringResource(id = R.string.event_creation_screen_beds),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag("beds_title"))
    OutlinedTextField(
        value = bedsText,
        onValueChange = { bedsText = it },
        label = { Text(stringResource(id = R.string.event_creation_screen_number_beds)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("n_beds"))
  }
}
