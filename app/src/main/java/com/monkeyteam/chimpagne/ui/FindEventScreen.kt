package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.LocationHelper
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import java.time.format.DateTimeFormatter.*
import java.util.Locale
import kotlinx.coroutines.launch

object FindEventScreens {
  const val FORM = 0
  const val MAP = 1
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun MainFindEventScreen(navObject: NavigationActions) {
  val pagerState = rememberPagerState { 2 }
  val coroutineScope = rememberCoroutineScope()

  val goToForm: () -> Unit = {
    coroutineScope.launch { pagerState.animateScrollToPage(FindEventScreens.FORM) }
  }

  val goToMap: () -> Unit = {
    coroutineScope.launch { pagerState.animateScrollToPage(FindEventScreens.MAP) }
  }

  ChimpagneTheme {
    HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
      when (page) {
        FindEventScreens.FORM -> FindEventFormScreen(navObject, goToMap)
        FindEventScreens.MAP -> FindEventMapScreen(goToForm)
      }
    }
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventFormScreen(navObject: NavigationActions, onSearchClick: () -> Unit) {

  var showDatePicker by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState()

  var tagInput by remember { mutableStateOf("") }
  val selectedTags = remember { mutableStateListOf<String>() }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Find an event") },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            })
      },
      bottomBar = {
        Button(
            onClick = { onSearchClick() },
            modifier =
                Modifier.fillMaxWidth().padding(8.dp).height(56.dp), // Typical height for buttons
            shape = MaterialTheme.shapes.extraLarge) {
              Icon(Icons.Rounded.Search, contentDescription = "Search")
              Spacer(Modifier.width(8.dp))
              Text("Search", style = MaterialTheme.typography.bodyLarge)
            }
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          Column(
              modifier =
                  Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.Start) {
                FindEventLegend("Enter Event Keywords", Icons.Rounded.Search, "Search")

                Spacer(Modifier.height(16.dp))

                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search...") },
                    modifier =
                        Modifier.fillMaxWidth()
                            .background(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceVariant))

                Spacer(Modifier.height(40.dp))

                FindEventLegend("Add Tags to your search", Icons.Rounded.Tag, "Tags")
                Spacer(Modifier.height(16.dp))

                TextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                              if (tagInput.trim().isNotBlank()) {
                                selectedTags.add(0, tagInput.trim().lowercase(Locale.getDefault()))
                                tagInput = ""
                              }
                            }),
                    maxLines = 1,
                    placeholder = { Text("Enter a tag...") },
                    modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                  selectedTags.forEach { tag -> TagChip(tag = tag) { selectedTags.remove(tag) } }
                }

                Spacer(Modifier.height(40.dp))

                FindEventLegend(
                    "Select the date of the event", Icons.Rounded.CalendarToday, "Select date")

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.align(Alignment.CenterHorizontally)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(100))
                            .background(
                                shape = RoundedCornerShape(100),
                                color = MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 24.dp, vertical = 16.dp)) {
                      Icon(Icons.Rounded.CalendarToday, contentDescription = "Select date")
                      Spacer(Modifier.width(8.dp))
                      Text("5 may 2024")
                    }
              }
        }
      }
  // Show date picker dialog when showDatePicker is true
  if (showDatePicker) {
    FindEventDatePicker(datePickerState) { showDatePicker = false }
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventDatePicker(datePickerState: DatePickerState, onDismissRequest: () -> Unit) {
  DatePickerDialog(
      onDismissRequest = { onDismissRequest() },
      confirmButton = {
        Button(
            onClick = {
              onDismissRequest()
              // Call onDateSelected with the date from datePickerState
              // This should be a LocalDateTime constructed from the selected date millis
            }) {
              Text("OK")
            }
      }) {
        DatePicker(
            state = datePickerState,
            // Add more customization as needed
        )
      }
}

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
  Surface(
      modifier = Modifier.padding(end = 8.dp),
      shape = RoundedCornerShape(52.dp),
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = tag,
              modifier = Modifier.padding(start = 16.dp),
              style = MaterialTheme.typography.bodyMedium)
          IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Remove tag")
          }
        }
      }
}

@Composable
fun FindEventLegend(text: String, imageVector: ImageVector, contentDescription: String) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(imageVector = imageVector, contentDescription = contentDescription)
    Text(
        text = text,
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.titleLarge,
    )
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventMapScreen(
    onBackIconClicked: () -> Unit,
    locationHelper: LocationHelper = LocationHelper()
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val coroutineScope = rememberCoroutineScope()

  val expandBottomSheet = { scope.launch { scaffoldState.bottomSheetState.expand() } }

  val addMarker = { location: Location ->
    coroutineScope.launch { locationHelper.addMarker(location) }
  }

  val removeMarker = { location: Location ->
    coroutineScope.launch { locationHelper.removeMarker(location) }
  }

  addMarker(Location("Balelec", 46.519144, 6.566804))

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = {
        Box(Modifier.fillMaxWidth().height(128.dp), contentAlignment = Alignment.Center) {
          Text("Swipe up to expand sheet")
        }
        Column(
            Modifier.fillMaxWidth().padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Sheet content")
              Spacer(Modifier.height(20.dp))
              Button(
                  onClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } }) {
                    Text("Join event")
                  }
            }
      },
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp) {
        Box(modifier = Modifier.padding(top = systemUiPadding.calculateTopPadding())) {
          MapContainer(locationHelper = locationHelper) { expandBottomSheet() }
          Column {
            FindEventSearchBar("Before Balelec", onBackIconClicked)
            TagsRow()
            DateRow()
          }
        }
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FindEventSearchBar(searchText: String, onBackIconClicked: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp)
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(100))
              .background(
                  color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(100))
              .padding(4.dp),
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onBackIconClicked() }) {
          Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back")
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = searchText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f))
      }
}

@Composable
fun TagsRow() {
  val tags = listOf("BBQ", "Student", "Vegan", "Beach")

  Row(
      modifier =
          Modifier.horizontalScroll(rememberScrollState())
              .padding(start = 8.dp, end = 0.dp, bottom = 8.dp, top = 8.dp)) {
        for (tag in tags) {
          TagChip(tag = tag)
        }
      }
}

@Composable
fun TagChip(tag: String) {
  Surface(
      modifier = Modifier.padding(start = 8.dp),
      shape = RoundedCornerShape(52.dp),
      color = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary) {
        Text(
            text = tag,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            style = MaterialTheme.typography.bodyMedium)
      }
}

@Composable
fun DateRow() {
  val date by remember { mutableStateOf("5 may 2024") }

  Row(
      modifier =
          Modifier.padding(start = 12.dp, top = 8.dp, end = 8.dp)
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(25))
              .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(25))
              .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Icon(Icons.Rounded.CalendarToday, contentDescription = "Select Date")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterVertically))
      }
}
