package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PRODUCTION_TABLES
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.LocationState
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.LocationIconTextButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.TopBar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel

class LocationViewModel(myContext: Context) {
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)

  fun startLocationUpdates(
      myContext: Context,
      onLocationSuccess: (lat: Double, lng: Double) -> Unit
  ) {
    if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let { onLocationSuccess(it.latitude, it.longitude) }
      }
    }
  }
}

@SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navObject: NavigationActions,
    accountViewModel: AccountViewModel,
    locationViewModel: LocationViewModel = LocationViewModel(myContext = LocalContext.current)
) {
  val N_CLOSEST = 4
  val context = LocalContext.current
  val uiState by accountViewModel.uiState.collectAsState()
  var showPromptLogin by remember { mutableStateOf(false) }
  var enableGPSButtonState by remember { mutableStateOf<LocationState>(LocationState.Idle) }

  val database = Database(PRODUCTION_TABLES)
  val findViewModel = FindEventsViewModel(database)
  val eventsNearMe = mutableListOf<ChimpagneEvent>()

  val closestEventsState = remember { mutableStateOf(listOf<ChimpagneEvent>()) }

  val fusedLocationProviderClient = remember {
    LocationServices.getFusedLocationProviderClient(context)
  }
  fun getClosestNEvent(
      li: List<ChimpagneEvent>,
      n: Int,
      myLocation: Location
  ): List<ChimpagneEvent> {
    if (li.size <= n) return li

    val sortedEvents =
        eventsNearMe.sortedBy { event ->
          val eventLocation = event.location
          myLocation.distanceTo(eventLocation)
        }

    return sortedEvents.take(n)
  }

  fun updateFeed() {
    var chimpagneAccountUID = ""
    if (accountViewModel.isUserLoggedIn()) {
      if (accountViewModel.uiState.value.currentUserUID != null) {
        chimpagneAccountUID = accountViewModel.uiState.value.currentUserUID!!
      }
    }

    findViewModel.fetchFeedEvents(
        onSuccess = {
          findViewModel.uiState.value.events.forEach { (_, u) -> eventsNearMe.add(u) }

          findViewModel.uiState.value.selectedLocation?.let {
            closestEventsState.value = getClosestNEvent(eventsNearMe, N_CLOSEST, it)
          }
        },
        onFailure = { Log.e("err", it.toString()) },
        chimpagneAccountUID)
  }

  fun getGPS() {
    enableGPSButtonState = LocationState.Searching
    fusedLocationProviderClient
        .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
        .addOnSuccessListener { location ->
          location?.let {
            findViewModel.updateSelectedLocation(Location("mylocation", it.latitude, it.longitude))
            enableGPSButtonState = LocationState.Set(it)
            updateFeed()
          }
        }
        .addOnFailureListener {
          enableGPSButtonState = LocationState.Error("Unable to get location: ${it.message}")
        }
  }
  val startForResult =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) getGPS()
          }

  val checkAndRequestGPS = {
    val locManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      val locReq = LocationRequest.create().apply { priority = Priority.PRIORITY_HIGH_ACCURACY }
      val builder = LocationSettingsRequest.Builder().addLocationRequest(locReq).setAlwaysShow(true)

      val client: SettingsClient = LocationServices.getSettingsClient(context)
      val checkLocationTask = client.checkLocationSettings(builder.build())

      checkLocationTask.addOnSuccessListener { getGPS() }

      checkLocationTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
          try {
            startForResult.launch(IntentSenderRequest.Builder(exception.resolution).build())
          } catch (_: IntentSender.SendIntentException) {}
        }
      }
    } else {
      getGPS()
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
          enableGPSButtonState = LocationState.Error("Location permission denied")
        }
      }

  Scaffold(
      topBar = {
        TopBar(
            text = context.getString(R.string.events_near_you),
            actions = {
              ProfileIcon(
                  uiState.currentUserProfilePicture,
                  onClick = {
                    if (!accountViewModel.isUserLoggedIn()) {
                      showPromptLogin = true
                    } else {
                      navObject.navigateTo(Route.ACCOUNT_SETTINGS_SCREEN)
                    }
                  })
            })
      }) { innerPadding ->
        if (showPromptLogin) {
          PromptLogin(context, navObject)
          showPromptLogin = false
        }

        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)) {
              // Events Feed taking the top half of the screen
              LazyColumn(
                  modifier =
                      Modifier.fillMaxHeight(0.5f)
                          .fillMaxWidth()
                          .background(MaterialTheme.colorScheme.surface)) {
                    if (closestEventsState.value.isEmpty()) {
                      item {
                        Text(
                            text = context.getString(R.string.turn_gps_on),
                            style =
                                MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.LightGray),
                            modifier = Modifier.padding(16.dp))
                        val requestLocationPermission = {
                          locationPermissionRequest.launch(
                              arrayOf(
                                  Manifest.permission.ACCESS_FINE_LOCATION,
                                  Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()) {
                              LocationIconTextButton(
                                  locationState = enableGPSButtonState,
                                  onClick = { requestLocationPermission() })
                            }
                      }
                    } else {
                      items(closestEventsState.value.sortedBy { it.startsAtTimestamp }) { event ->
                        EventCard(
                            event,
                            onClick = { navObject.navigateTo(Route.EVENT_SCREEN + "/${event.id}") })
                      }
                    }
                  }

              // Buttons taking the bottom half of the screen
              Column(
                  modifier =
                      Modifier.fillMaxHeight(0.5f).fillMaxWidth().align(Alignment.BottomCenter),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    ChimpagneButton(
                        modifier = Modifier.fillMaxWidth().testTag("open_events_button"),
                        onClick = {
                          if (!accountViewModel.isUserLoggedIn()) {
                            showPromptLogin = true
                          } else {
                            navObject.navigateTo(Route.MY_EVENTS_SCREEN)
                          }
                        },
                        text = stringResource(id = R.string.homescreen_my_events),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val permissionLauncher =
                        rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission()) { isGranted ->
                              if (isGranted) {
                                findViewModel.updateSelectedLocation(
                                    Location("initialLocation", 0.0, 0.0))
                                locationViewModel.startLocationUpdates(context) { lat, lng ->
                                  findViewModel.updateSelectedLocation(
                                      Location("mylocation", lat, lng))
                                  updateFeed()
                                }
                              }
                            }
                    LaunchedEffect(Unit) {
                      permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                    ChimpagneButton(
                        modifier = Modifier.fillMaxWidth().testTag("discover_events_button"),
                        onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                        text = stringResource(R.string.homescreen_join_event),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ChimpagneButton(
                        modifier = Modifier.fillMaxWidth().testTag("organize_event_button"),
                        onClick = {
                          if (!accountViewModel.isUserLoggedIn()) {
                            showPromptLogin = true
                          } else {
                            navObject.navigateTo(Route.EVENT_CREATION_SCREEN)
                          }
                        },
                        text = stringResource(R.string.homescreen_organize_event),
                    )
                  }
            }
      }
}
