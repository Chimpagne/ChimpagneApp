package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.compose.rememberCameraPositionState
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.LocationState
import com.monkeyteam.chimpagne.ui.components.DateRangeSelector
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.LocationSelector
import com.monkeyteam.chimpagne.ui.components.TagField
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.theme.CustomGreen
import com.monkeyteam.chimpagne.ui.theme.CustomOrange
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
  const val DETAIL = 2
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

  var currentEvent by remember { mutableStateOf<ChimpagneEvent?>(null) }
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

  val goToDetail: (ChimpagneEvent) -> Unit = { event ->
    currentEvent = event
    coroutineScope.launch { pagerState.scrollToPage(FindEventScreens.DETAIL) }
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

  HorizontalPager(state = pagerState, userScrollEnabled = false, beyondBoundsPageCount = 2) { page
    ->
    when (page) {
      FindEventScreens.FORM ->
          FindEventFormScreen(navObject, findViewModel, fetchEvents, showToast, displayResult)
      FindEventScreens.MAP ->
          FindEventMapScreen(goToForm, findViewModel, goToDetail, accountViewModel, navObject)
      FindEventScreens.DETAIL -> null
    }
  }
}

@SuppressLint("MissingPermission")
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

  var locationState by remember { mutableStateOf<LocationState>(LocationState.Idle) }

  val uiState by findViewModel.uiState.collectAsState()
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var tagFieldActive by remember { mutableStateOf(false) }

  val fusedLocationProviderClient = remember {
    LocationServices.getFusedLocationProviderClient(context)
  }

  val getLocation = {
    showToast(context.getString(R.string.find_event_event_locate_searching))
    locationState = LocationState.Searching
    fusedLocationProviderClient
        .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
        .addOnSuccessListener { location ->
          location?.let {
            showToast(context.getString(R.string.find_event_location_set))
            findViewModel.updateSelectedLocation(Location("mylocation", it.latitude, it.longitude))
            locationState = LocationState.Set(it)
          } ?: showToast(context.getString(R.string.find_event_location_not_found))
        }
        .addOnFailureListener {
          showToast(context.getString(R.string.find_event_location_not_found) + " : ${it.message}")
          locationState = LocationState.Error("Unable to get location: ${it.message}")
        }
  }

  val startForResult =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) getLocation()
            else showToast(context.getString(R.string.find_event_location_not_found))
          }

  val checkAndRequestGPS = {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

      Log.d("FindEventFormScreen", "GPS asked to be turned on")

      // GPS is not enabled, proceed to ask user to enable it
      val locationRequest =
          LocationRequest.create().apply { priority = Priority.PRIORITY_HIGH_ACCURACY }
      val builder =
          LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true)

      val client: SettingsClient = LocationServices.getSettingsClient(context)
      val task = client.checkLocationSettings(builder.build())

      task.addOnSuccessListener { getLocation() }

      task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
          try {
            startForResult.launch(IntentSenderRequest.Builder(exception.resolution).build())
          } catch (_: IntentSender.SendIntentException) {}
        }
      }
    } else {
      Log.d("FindEventFormScreen", "GPS already turned on")
      getLocation()
    }
  }

  val locationPermissionRequest =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
          // Permissions granted, now check if GPS is enabled and request enabling if necessary.
          checkAndRequestGPS()
        } else {
          showToast(context.getString(R.string.permission_denied))
          locationState = LocationState.Error("Location permission denied")
        }
      }

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
              showToast(context.getString(R.string.permission_denied))
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
              uid,
              onSuccess = showScannedEvent,
              onFailure = { showToast(context.getString(R.string.find_event_no_result)) })
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
                    stringResource(id = R.string.find_event_search_button_text).uppercase(),
                    style = ChimpagneTypography.bodyLarge)
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
                    text =
                        when (locationState) {
                          is LocationState.Set ->
                              stringResource(id = R.string.find_event_location_set)
                          is LocationState.Searching ->
                              stringResource(id = R.string.find_event_event_locate_searching)
                          else -> stringResource(id = R.string.find_event_event_locate_me_button)
                        },
                    icon =
                        when (locationState) {
                          is LocationState.Set -> Icons.Rounded.CheckCircle
                          is LocationState.Searching -> Icons.Default.HourglassEmpty
                          else -> Icons.Rounded.MyLocation
                        },
                    color =
                        when (locationState) {
                          is LocationState.Set -> CustomGreen
                          is LocationState.Searching -> CustomOrange
                          else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                    onClick = { requestLocationPermission() },
                    modifier =
                        Modifier.align(Alignment.CenterHorizontally)
                            .testTag("request_location_permission_button"))
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
                    modifier = Modifier.fillMaxWidth().testTag("find_slider"))

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

                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("sel_date"),
                    selectDateRange = findViewModel::updateDateRange)

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
    onEventClick: (ChimpagneEvent) -> Unit,
    accountViewModel: AccountViewModel,
    navObject: NavigationActions
) {

  val uiState by findViewModel.uiState.collectAsState()

  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val coroutineScope = rememberCoroutineScope()
  var currentEvents by remember { mutableStateOf<List<ChimpagneEvent>>(listOf()) }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.5196, 6.6323), 10f)
  }
  val onMarkerClick: (Cluster<MarkerData>) -> Unit = { markers ->
    coroutineScope.launch {
      currentEvents = markers.items.mapNotNull { marker -> uiState.events[marker.id] }
      launch {
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(markers.position))
        scaffoldState.bottomSheetState.expand()
      }
    }
  }

  LaunchedEffect(uiState.events.size) { currentEvents = uiState.events.values.toList() }

  val goBack = {
    scope.launch {
      scaffoldState.bottomSheetState.partialExpand()
      findViewModel.eraseResults()
      onBackIconClicked()
    }
  }

  val onJoinClick: (ChimpagneEvent) -> Unit = { event ->
    Toast.makeText(context, "Joining ${event.title}", Toast.LENGTH_SHORT).show()
    findViewModel.joinEvent(
        event.id,
        { Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show() },
        { Toast.makeText(context, "FAILURE", Toast.LENGTH_SHORT).show() })
  }

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = { DetailScreenListSheet(events = currentEvents, onEventClick, context) },
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
