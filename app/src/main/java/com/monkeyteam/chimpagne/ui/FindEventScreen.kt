package com.monkeyteam.chimpagne.ui

import DateSelector
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.TagField
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import kotlinx.coroutines.launch

object FindEventScreens {
  const val FORM = 0
  const val MAP = 1
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun MainFindEventScreen(
    navObject: NavigationActions,
    findViewModel: FindEventsViewModel = viewModel()
) {
  val pagerState = rememberPagerState { 2 }
  val coroutineScope = rememberCoroutineScope()

  val goToForm: () -> Unit = {
    coroutineScope.launch { pagerState.animateScrollToPage(FindEventScreens.FORM) }
  }

  val goToMap: () -> Unit = {
    coroutineScope.launch {
      findViewModel.fetchEvents()
      pagerState.animateScrollToPage(FindEventScreens.MAP)
    }
  }

  HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
    when (page) {
      FindEventScreens.FORM -> FindEventFormScreen(navObject, findViewModel, goToMap)
      FindEventScreens.MAP -> FindEventMapScreen(goToForm, findViewModel)
    }
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventFormScreen(
    navObject: NavigationActions,
    findViewModel: FindEventsViewModel,
    onSearchClick: () -> Unit
) {

  val uiState by findViewModel.uiState.collectAsState()

  val context = LocalContext.current

  val scrollState = rememberScrollState()

  var tagFieldActive by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(stringResource(id = R.string.find_event_page_title)) },
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
                Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .height(56.dp)
                    .testTag("button_search"), // Typical height for buttons
            shape = MaterialTheme.shapes.extraLarge) {
              Icon(Icons.Rounded.Search, contentDescription = "Search")
              Spacer(Modifier.width(8.dp))
              Text(
                  stringResource(id = R.string.find_event_search_button_text),
                  style = MaterialTheme.typography.bodyLarge)
            }
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState),
              horizontalAlignment = Alignment.Start) {
                Legend(
                    stringResource(id = R.string.find_event_event_location_legend),
                    Icons.Rounded.LocationOn,
                    "Location")

                Spacer(Modifier.height(16.dp))

                LocationSelector(
                    uiState.selectedLocation,
                    findViewModel::updateSelectedLocation,
                    Modifier.fillMaxWidth().testTag("input_location"))

                Spacer(Modifier.height(16.dp))
                IconTextButton(
                    text = stringResource(id = R.string.find_event_event_locate_me_button),
                    icon = Icons.Rounded.MyLocation,
                    onClick = {
                      Toast.makeText(
                              context,
                              context.getString(R.string.find_event_near_me_toast),
                              Toast.LENGTH_SHORT)
                          .show()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("sel_location"))
                Spacer(Modifier.height(16.dp))

                Text(
                    text =
                        stringResource(id = R.string.find_event_search_radius) +
                            " : ${uiState.radiusAroundLocationInM.toInt() / 1000} km")

                Slider(
                    value = uiState.radiusAroundLocationInM.toFloat() / 1000,
                    onValueChange = {
                      findViewModel.updateLocationSearchRadius(it.toDouble() * 1000)
                    },
                    valueRange = 1f..30f,
                    modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(32.dp))

                Legend(
                    stringResource(id = R.string.find_event_event_tags_legend),
                    Icons.Rounded.Tag,
                    "Tags")

                Spacer(Modifier.height(16.dp))

                TagField(
                    uiState.selectedTags,
                    findViewModel::updateTags,
                    { tagFieldActive = it },
                    Modifier.fillMaxWidth())

                Spacer(Modifier.height(40.dp))

                Legend(
                    stringResource(id = R.string.find_event_date_legend),
                    Icons.Rounded.CalendarToday,
                    "Select date")

                Spacer(Modifier.height(16.dp))

                DateSelector(
                    uiState.selectedDate,
                    findViewModel::updateSelectedDate,
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("sel_date"))

                if (tagFieldActive) {
                  Spacer(modifier = Modifier.height(250.dp))
                  LaunchedEffect(Unit) { scrollState.animateScrollTo(scrollState.maxValue) }
                }
              }
        }
      }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventMapScreen(
    onBackIconClicked: () -> Unit,
    findViewModel: FindEventsViewModel,
) {

  val uiState by findViewModel.uiState.collectAsState()

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
      currentEvent = uiState.events[marker.tag as String]
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

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = { EventDetailSheet(event = currentEvent) },
      scaffoldState = scaffoldState,
      modifier = Modifier.testTag("map_screen"),
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
                isMapInitialized = true,
                events = uiState.events)
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
              text = event.startsAt().time.toString(),
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.endsAt().time.toString(),
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
                Text(stringResource(id = R.string.find_event_join_event_button_text))
              }
        }
  } else {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
      Text(
          stringResource(id = R.string.find_event_no_event_available),
          style = MaterialTheme.typography.bodyMedium)
    }
  }
}
