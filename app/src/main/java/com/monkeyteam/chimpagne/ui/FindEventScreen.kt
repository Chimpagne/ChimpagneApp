package com.monkeyteam.chimpagne.ui

import DateSelector
import android.util.Log
import android.widget.AutoCompleteTextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.Timestamp
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.AutoCompleteTextView
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import com.monkeyteam.chimpagne.ui.utilities.MapViewModel
import java.text.DateFormat
import java.time.format.DateTimeFormatter.*
import java.util.Calendar
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

  val scrollState = rememberScrollState()

  var searchActive by remember { mutableStateOf(false) }

  var showDatePicker by remember { mutableStateOf(false) }

  var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

  val keyboardController = LocalSoftwareKeyboardController.current

  var tagInput by remember { mutableStateOf("") }
  val selectedTags = remember { mutableStateListOf<String>() }

  val tagPredictions =
      remember(tagInput) {
        if (tagInput.isEmpty()) {
          listOf() // Return an empty list if addressInput is empty
        } else {
          listOf(
                  "student",
                  "disco",
                  "vegan",
                  "dark",
                  "alcool",
                  "beer",
                  "bbq",
                  "soundboks",
                  "carnaval")
              .filter { it.contains(tagInput, ignoreCase = true) }
              .take(5)
        }
      }

  var searchRadius by remember { mutableStateOf(1f) }
  val view = LocalView.current
  var addressInput by remember { mutableStateOf("") }
  val selectedAddress = remember { mutableStateListOf<String>() }

  val addressPredictions =
      remember(addressInput) {
        if (addressInput.isEmpty()) {
          listOf() // Return an empty list if addressInput is empty
        } else {
          listOf("EPFL", "UNIL", "Lausanne", "Renens", "Ecublens", "Montreux", "DLL", "Rolex")
              .filter { it.contains(addressInput, ignoreCase = true) }
              .take(5)
        }
      }

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
              modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
              horizontalAlignment = Alignment.Start) {
                FindEventLegend("Event Location", Icons.Rounded.Search, "Search")

                Spacer(Modifier.height(16.dp))

                AutoCompleteTextView(
                    modifier = Modifier.fillMaxWidth(),
                    query = addressInput,
                    queryLabel = "Enter an address",
                    onQueryChanged = { updatedAddress -> addressInput = updatedAddress },
                    predictions = addressPredictions,
                    onClearClick = {
                      if (addressInput === "") {
                        view.clearFocus()
                        keyboardController?.hide()
                      } else {
                        addressInput = ""
                      }
                    },
                    onDoneActionClick = {
                      // TODO : should be an address converted to location using Nominatim
                      if (addressInput.trim().isNotBlank()) {
                        selectedAddress.add(0, addressInput.trim().lowercase(Locale.getDefault()))
                        addressInput = ""
                      } else {
                        view.clearFocus()
                      }
                    },
                    onItemClick = { selectedAdd ->
                      // TODO : should be an address fetched from Nominatim
                      if (selectedAdd.trim().isNotBlank()) {
                        selectedAddress.add(0, selectedAdd.trim().lowercase(Locale.getDefault()))
                        addressInput = ""
                      }
                    }) { address ->
                      // Define how the items need to be displayed
                      Text(address, fontSize = 14.sp)
                    }

                Spacer(Modifier.height(16.dp))
                IconTextButton(
                    text = "Locate me",
                    icon = Icons.Rounded.MyLocation,
                    onClick = { /*TODO*/},
                    modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(16.dp))

                Text(text = "Search Radius: ${searchRadius.toInt()} km")

                Slider(
                    value = searchRadius,
                    onValueChange = { searchRadius = it },
                    valueRange = 1f..30f,
                    modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(32.dp))

                FindEventLegend("Add Tags to your search", Icons.Rounded.Tag, "Tags")

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                  selectedTags.forEach { tag -> TagChip(tag = tag) { selectedTags.remove(tag) } }
                }

                Spacer(Modifier.height(16.dp))

                AutoCompleteTextView(
                    modifier = Modifier.fillMaxWidth(),
                    query = tagInput,
                    queryLabel = "Enter a tag",
                    onQueryChanged = { updatedTag ->
                      tagInput = updatedTag
                      // Update addressPlaceItemPredictions based on the tagInput here
                    },
                    predictions = tagPredictions,
                    onClearClick = {
                      Log.d(tagInput, "tagInput")
                      if (tagInput === "") {
                        keyboardController?.hide()
                        view.clearFocus()
                      } else {
                        tagInput = ""
                      }
                    },
                    onDoneActionClick = {
                      if (tagInput.trim().isNotBlank()) {
                        selectedTags.add(0, tagInput.trim().lowercase(Locale.getDefault()))
                        tagInput = ""
                      } else {
                        view.clearFocus()
                      }
                    },
                    onItemClick = { selectedTag ->
                      if (selectedTag.trim().isNotBlank()) {
                        selectedTags.add(0, selectedTag.trim().lowercase(Locale.getDefault()))
                        tagInput = ""
                      }
                    },
                    onFocusChanged = { isFocused -> searchActive = isFocused }) { tag ->
                      // Define how the items need to be displayed
                      Text(tag, fontSize = 14.sp)
                    }

                Spacer(Modifier.height(40.dp))

                FindEventLegend("Date of the event", Icons.Rounded.CalendarToday, "Select date")

                Spacer(Modifier.height(16.dp))

                IconTextButton(
                    text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(selectedDate.time),
                    icon = Icons.Rounded.CalendarToday,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally))

                if (searchActive) {
                  Spacer(modifier = Modifier.height(250.dp))
                  LaunchedEffect(Unit) { scrollState.animateScrollTo(scrollState.maxValue) }
                }
              }
        }
      }
  // Show date picker dialog when showDatePicker is true
  if (showDatePicker) {
    DateSelector(selectedDate, { showDatePicker = false }, { calendar -> selectedDate = calendar })
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
fun SimpleTagChip(tag: String) {
  Text(
      text = "#$tag",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary)
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
    mapViewModel: MapViewModel = MapViewModel(),
) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val coroutineScope = rememberCoroutineScope()
  var isMapInitialized by remember { mutableStateOf(false) }
  var currentEvent by remember { mutableStateOf<ChimpagneEvent?>(null) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.5196, 6.6323), 10f)
  }
  val onMarkerClick: (Marker) -> Unit = { marker ->
    coroutineScope.launch {
      currentEvent = mapViewModel.getEventById(marker.tag as String)
      launch { scaffoldState.bottomSheetState.expand() }
      launch { cameraPositionState.animate(CameraUpdateFactory.newLatLng(marker.position)) }
    }
  }

  val goBack = {
    scope.launch {
      scaffoldState.bottomSheetState.partialExpand()
      onBackIconClicked()
    }
  }

  val addEvents = { list: List<ChimpagneEvent> ->
    coroutineScope.launch {
      mapViewModel.clearMarkers()
      list.forEach { event -> mapViewModel.addMarker(event) }
    }
  }

  addEvents(
      listOf(
          ChimpagneEvent(
              id = "c",
              title = "Balelec",
              description = "The best student party in Switzerland",
              location = Location("Balelec", 46.519144, 6.566804),
              isPublic = true,
              tags = listOf("student", "disco", "vegan"),
              startsAtTimestamp = Timestamp.now(),
              endsAtTimestamp = Timestamp.now()),
          ChimpagneEvent(
              id = "b",
              title = "AirSound",
              description = "The best sound system in the world",
              location = Location("AirSound", 46.559144, 6.566804),
              isPublic = true,
              tags = listOf("soundboks", "alcool", "beer"),
              startsAtTimestamp = Timestamp.now(),
              endsAtTimestamp = Timestamp.now()),
          ChimpagneEvent(
              id = "a",
              title = "Anniversaire de Juan",
              description = "The best birthday party in the world",
              location = Location("Anniversaire de Juan", 46.51644, 6.53804),
              isPublic = true,
              tags = listOf("carnaval", "bbq"),
              startsAtTimestamp = Timestamp.now(),
              endsAtTimestamp = Timestamp.now())))

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = { EventDetailSheet(event = currentEvent) },
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp) {
        DisposableEffect(Unit) {
          isMapInitialized = true
          onDispose { isMapInitialized = false }
        }

        Box(modifier = Modifier.padding(top = systemUiPadding.calculateTopPadding())) {
          if (isMapInitialized) {
            MapContainer(
                bottomSheetState = scaffoldState.bottomSheetState,
                cameraPositionState = cameraPositionState,
                onMarkerClick = onMarkerClick,
                mapViewModel = mapViewModel,
                isMapInitialized = true)
          }

          IconButton(
              modifier =
                  Modifier.padding(start = 12.dp, top = 12.dp)
                      .shadow(elevation = 4.dp, shape = RoundedCornerShape(100))
                      .background(
                          color = MaterialTheme.colorScheme.surface,
                          shape = RoundedCornerShape(100))
                      .padding(4.dp),
              onClick = { goBack() }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back")
              }
        }
      }
}

@Composable
fun EventDetailSheet(event: ChimpagneEvent?) {
  if (event != null) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = event.title,
              style = MaterialTheme.typography.headlineMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.startAt.time.toString(),
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

        Text(
            text = event.endsAt.time.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.description,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 8.dp))

          Row(
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                event.tags.forEach { tag -> SimpleTagChip(tag) }
              }

          Button(
              onClick = { /* Handle join event */},
              modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Join event")
              }
        }
  } else {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
      Text("No event details available", style = MaterialTheme.typography.bodyMedium)
    }
  }
}
