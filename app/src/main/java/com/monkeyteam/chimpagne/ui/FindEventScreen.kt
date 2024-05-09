package com.monkeyteam.chimpagne.ui

import DateSelector
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.QrCodeScanner
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.components.TagField
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import com.monkeyteam.chimpagne.ui.utilities.MarkerData
import com.monkeyteam.chimpagne.ui.utilities.QRCodeScanner
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
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
    findViewModel: FindEventsViewModel,
    accountViewModel: AccountViewModel
) {
  val pagerState = rememberPagerState { 2 }
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  var toast: Toast? by remember { mutableStateOf(null) }

  val showToast: (String) -> Unit = { message ->
    toast?.cancel()
    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply { show() }
  }

  val noResultToast: () -> Unit = { showToast(context.getString(R.string.find_event_no_result)) }
  val noSelectedLocationToast: () -> Unit = {
    showToast(context.getString(R.string.find_event_location_not_selected))
  }

  val goToForm: () -> Unit = {
    coroutineScope.launch { pagerState.scrollToPage(FindEventScreens.FORM) }
    findViewModel.setLoading(false)
  }

  val displayResult: () -> Unit = {
    coroutineScope.launch { pagerState.scrollToPage(FindEventScreens.MAP) }
  }

  val fetchEvents: () -> Unit = {
    if (findViewModel.uiState.value.selectedLocation == null) {
      noSelectedLocationToast()
    } else {
      coroutineScope.launch {
        findViewModel.fetchEvents(onSuccess = { displayResult() }, onFailure = { noResultToast() })
      }
    }
  }

  HorizontalPager(state = pagerState, userScrollEnabled = false, beyondBoundsPageCount = 1) { page
    ->
    when (page) {
      FindEventScreens.FORM ->
          FindEventFormScreen(navObject, findViewModel, fetchEvents, showToast, displayResult)
      FindEventScreens.MAP ->
          FindEventMapScreen(goToForm, findViewModel, accountViewModel, navObject)
    }
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventFormScreen(
    navObject: NavigationActions,
    findViewModel: FindEventsViewModel,
    onSearchClick: () -> Unit,
    showToast: (String) -> Unit,
    showScannedEvent: () -> Unit
) {

  var showDialog by remember { mutableStateOf(false) }

  val uiState by findViewModel.uiState.collectAsState()
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var tagFieldActive by remember { mutableStateOf(false) }

  val fusedLocationProviderClient = remember {
    LocationServices.getFusedLocationProviderClient(context)
  }

  val locationPermissionRequest =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            when {
              permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                  permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                  showToast("Location permission denied")
                  return@rememberLauncherForActivityResult
                }

                showToast("Getting location")
                fusedLocationProviderClient
                    .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
                    .addOnSuccessListener { location ->
                      location?.let {
                        showToast("Location OK")
                        findViewModel.updateSelectedLocation(
                            Location("mylocation", it.latitude, it.longitude))
                      }
                    }
                    .addOnFailureListener { showToast("Unable to get location: ${it.message}") }
              }
              else -> showToast("Location permission denied")
            }
          })

  val requestLocationPermission = {
    locationPermissionRequest.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
  }

  val cameraPermissionRequest =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { granted ->
            if (granted) {
              showDialog = true
            } else {
              showToast("Camera permission denied")
            }
          })

  val requestCameraPermission = { cameraPermissionRequest.launch(Manifest.permission.CAMERA) }
  if (showDialog) {
    QRCodeScanner(
        { showDialog = false },
        {
          showDialog = false

          val uid = it.substringAfter("?uid=")

          findViewModel.fetchEvent(
              uid, onSuccess = showScannedEvent, onFailure = { showToast("Event not found") })
        })
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(stringResource(id = R.string.find_event_page_title)) },
            modifier = Modifier.shadow(4.dp).testTag("find_event_title"),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            },
            actions = {
              IconButton(onClick = requestCameraPermission) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scan QR",
                    modifier = Modifier.testTag("qr_button"))
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
              if (uiState.loading) {
                SpinnerView(MaterialTheme.colorScheme.onPrimary)
              } else {
                Icon(Icons.Rounded.Search, contentDescription = "Search")
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(id = R.string.find_event_search_button_text),
                    style = MaterialTheme.typography.bodyLarge)
              }
            }
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).testTag("find_event_form_screen")) {
          Column(
              modifier =
                  Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(scrollState),
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
                    onClick = { requestLocationPermission() },
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
    accountViewModel: AccountViewModel,
    navObject: NavigationActions
) {

  val uiState by findViewModel.uiState.collectAsState()

  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val coroutineScope = rememberCoroutineScope()
  var currentEvent by remember { mutableStateOf<ChimpagneEvent?>(null) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.5196, 6.6323), 10f)
  }
  val onMarkerClick: (MarkerData) -> Unit = { markerData ->
    coroutineScope.launch {
      currentEvent = uiState.events[markerData.id]
      launch { cameraPositionState.animate(CameraUpdateFactory.newLatLng(markerData.position)) }

      launch { scaffoldState.bottomSheetState.expand() }
    }
  }

  LaunchedEffect(uiState.events.size) {
    if (uiState.events.size == 1) {
      currentEvent = uiState.events.values.first()
      scaffoldState.bottomSheetState.expand()
    }
  }

  val goBack = {
    scope.launch {
      scaffoldState.bottomSheetState.partialExpand()
      findViewModel.eraseResults()
      onBackIconClicked()
    }
  }

  val onJoinClick: () -> Unit = {
    if (currentEvent != null) {
      Toast.makeText(context, "Joining ${currentEvent?.title}", Toast.LENGTH_SHORT).show()
      findViewModel.joinEvent(
          currentEvent!!.id,
          { Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show() },
          { Toast.makeText(context, "FAILURE", Toast.LENGTH_SHORT).show() })
    }
  }

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = { DetailScreenSheet(event = currentEvent, onJoinClick) },
      scaffoldState = scaffoldState,
      modifier = Modifier.testTag("map_screen"),
      sheetPeekHeight = 0.dp) {
        Box(modifier = Modifier.padding(top = systemUiPadding.calculateTopPadding())) {
          MapContainer(
              cameraPositionState = cameraPositionState,
              onMarkerClick = onMarkerClick,
              isMapInitialized = true,
              events = uiState.events,
              radius = uiState.radiusAroundLocationInM,
              startingPosition = uiState.selectedLocation)

          IconButton(
              modifier =
                  Modifier.padding(start = 12.dp, top = 12.dp)
                      .testTag("go_back")
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
