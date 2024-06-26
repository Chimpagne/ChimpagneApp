package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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
import com.monkeyteam.chimpagne.model.feed.getClosestNEvent
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.model.location.LocationState
import com.monkeyteam.chimpagne.model.location.LocationViewModel
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.LocationIconTextButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.TopBar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.promptLogin
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel

/** We use this SuppressLint annotation to be able the case where the user is not logged in. */
@SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
@Composable
fun HomeScreen(
    navObject: NavigationActions,
    accountViewModel: AccountViewModel,
    locationViewModel: LocationViewModel = LocationViewModel(myContext = LocalContext.current)
) {
  val context = LocalContext.current
  val uiState by accountViewModel.uiState.collectAsState()
  var enableGPSButtonState by remember { mutableStateOf<LocationState>(LocationState.Idle) }

  val database = Database(PRODUCTION_TABLES)
  val findViewModel = FindEventsViewModel(database)
  val eventsNearMe = mutableListOf<ChimpagneEvent>()

  val closestEventsState = remember { mutableStateOf(listOf<ChimpagneEvent>()) }

  val fusedLocationProviderClient = remember {
    LocationServices.getFusedLocationProviderClient(context)
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
            closestEventsState.value = getClosestNEvent(eventsNearMe, it)
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
                      promptLogin(context, navObject)
                    } else {
                      navObject.navigateTo(Route.ACCOUNT_SETTINGS_SCREEN)
                    }
                  },
                  modifier = Modifier.testTag("account_settings_button"))
            })
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { navObject.navigateTo(Route.EVENT_CREATION_SCREEN) },
            content = { Icon(Icons.Filled.Add, "organize an event") },
            modifier = Modifier.testTag("organize_event_button"))
      }) { innerPadding ->
        LazyColumn(Modifier.padding(innerPadding)) {
          if (closestEventsState.value.isEmpty()) {
            item {
              Text(
                  text = context.getString(R.string.turn_gps_on),
                  style = MaterialTheme.typography.headlineLarge.copy(color = Color.LightGray),
                  modifier = Modifier.padding(16.dp))
              val requestLocationPermission = {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
              }
              Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                LocationIconTextButton(
                    locationState = enableGPSButtonState, onClick = { requestLocationPermission() })
              }
            }
          } else {
            items(closestEventsState.value) { event ->
              EventCard(
                  event, onClick = { navObject.navigateTo(Route.EVENT_SCREEN + "/${event.id}") })
            }
          }
        }

        val permissionLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()) { isGranted ->
                  if (isGranted) {
                    findViewModel.updateSelectedLocation(Location("initialLocation", 0.0, 0.0))
                    locationViewModel.startLocationUpdates(context) { lat, lng ->
                      findViewModel.updateSelectedLocation(Location("mylocation", lat, lng))
                      updateFeed()
                    }
                  }
                }
        LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
      }
}
