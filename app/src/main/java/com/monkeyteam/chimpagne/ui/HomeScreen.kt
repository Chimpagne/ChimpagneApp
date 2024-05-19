package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TopAppBar
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
import com.google.android.gms.location.LocationServices
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PUBLIC_TABLES
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
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

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navObject: NavigationActions,
    accountViewModel: AccountViewModel,
    locationViewModel: LocationViewModel = LocationViewModel(myContext = LocalContext.current)
) {

  val context = LocalContext.current
  val uiState by accountViewModel.uiState.collectAsState()
  var showPromptLogin by remember { mutableStateOf(false) }

  val database = Database(PUBLIC_TABLES)
  val findViewModel = FindEventsViewModel(database)
  val eventsNearMe = mutableListOf<ChimpagneEvent>()
  val closestEventsState = remember { mutableStateOf(listOf<ChimpagneEvent>()) }
  fun getClosestNEvent(
      li: List<ChimpagneEvent>,
      n: Int,
      myLocation: Location
  ): List<ChimpagneEvent> {
    if (li.size <= n) {
      return li
    }

    val sortedEvents =
        eventsNearMe.sortedBy { event ->
          val eventLocation = event.location
          myLocation.distanceTo(eventLocation)
        }

    return sortedEvents.take(n)
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("") },
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
        val N_CLOSEST = 4

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
                    item {
                      Text(
                          text = context.getString(R.string.events_near_you),
                          style = MaterialTheme.typography.headlineLarge,
                          modifier = Modifier.padding(16.dp))
                    }

                    if (closestEventsState.value.isEmpty()) {
                      item {
                        Text(
                            text = context.getString(R.string.turn_gps_on),
                            style =
                                MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.LightGray),
                            modifier = Modifier.padding(16.dp))
                      }
                    } else {
                      items(closestEventsState.value) { event ->
                        EventCard(
                            event,
                            onClick = {
                              navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}")
                            })
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
                        modifier = Modifier.testTag("open_events_button"),
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
                                  var chimpagneAccountUID = ""
                                  if (accountViewModel.isUserLoggedIn()) {
                                    if (accountViewModel.uiState.value.currentUserUID != null) {
                                      chimpagneAccountUID =
                                          accountViewModel.uiState.value.currentUserUID!!
                                      Toast.makeText(
                                              context, chimpagneAccountUID, Toast.LENGTH_SHORT)
                                          .show()
                                    }
                                  }

                                  findViewModel.fetchAroundLocation(
                                      onSuccess = {
                                        findViewModel.uiState.value.events.forEach { (_, u) ->
                                          eventsNearMe.add(u)
                                        }

                                        findViewModel.uiState.value.selectedLocation?.let {
                                          closestEventsState.value =
                                              getClosestNEvent(eventsNearMe, N_CLOSEST, it)
                                        }
                                      },
                                      onFailure = { Log.e("err", it.toString()) },
                                      chimpagneAccountUID)
                                }
                              }
                            }
                    LaunchedEffect(Unit) {
                      permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                    ChimpagneButton(
                        modifier = Modifier.testTag("discover_events_button"),
                        onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                        text = stringResource(R.string.homescreen_join_event),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ChimpagneButton(
                        modifier = Modifier.testTag("organize_event_button"),
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
