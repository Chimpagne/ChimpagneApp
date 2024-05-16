package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.Database
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
        location?.let {
          println("UPDATING LOCATION WITH VALUES")
          println(it.latitude)
          onLocationSuccess(it.latitude, it.longitude)
        }
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

  val database = Database()
  val findViewModel = FindEventsViewModel(database)
  val eventsNearMe = mutableListOf<ChimpagneEvent>()
  var closestEvents = listOf<ChimpagneEvent>()
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

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Column() {
                Text(
                    text = "Events near you",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp))
                LazyColumn() {
                  items(closestEventsState.value) { event ->
                    EventCard(
                        event,
                        onClick = {
                          navObject.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}")
                        })
                  }
                }
              }

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
                          locationViewModel.startLocationUpdates(context) { lat, lng ->
                            findViewModel.updateSelectedLocation(Location("mylocation", lat, lng))

                            findViewModel.fetchAroundLocation(
                                onSuccess = {
                                  findViewModel.uiState.value.events.forEach { (t, u) ->
                                    eventsNearMe.add(u)
                                  }

                                  findViewModel.uiState.value.selectedLocation?.let {
                                    closestEventsState.value = getClosestNEvent(eventsNearMe, 4, it)
                                    println("CLOSEST EVENTS")

                                    /*
                                    For example, closestEvents might contains a list of ChimpagneEvent like this:
                                    ChimpagneEvent(id=SECOND_EVENT, title=Second event, description=I love bananas, location=Location(name=EPFL, latitude=46.51913, longitude=6.56758, geohash=u0k8tkw3ed), public=true, tags=[bananas, monkeys], guests={}, staffs={}, startsAtTimestamp=Timestamp(seconds=1718032500, nanoseconds=758000000), endsAtTimestamp=Timestamp(seconds=1718118900, nanoseconds=758000000), ownerId=JUAN, supplies={1=c 1 h, 2=kk 2 j, 3=gbn 3 h}, parkingSpaces=10, beds=5)
                                    ChimpagneEvent(id=FIRST_EVENT, title=First event, description=a random description, location=Location(name=EPFL, latitude=46.519124, longitude=6.567593, geohash=u0k8tkw3s1), public=true, tags=[vegan, monkeys], guests={}, staffs={}, startsAtTimestamp=Timestamp(seconds=1717946100, nanoseconds=757000000), endsAtTimestamp=Timestamp(seconds=1718118900, nanoseconds=757000000), ownerId=JUAN, supplies={1=d 1 g, 2=ff 2 d, 3=ee 3 e}, parkingSpaces=1, beds=2)

                                     */
                                    closestEventsState.value.forEach { e -> println(e) }
                                  }
                                },
                                onFailure = { println("couscous") })
                          }
                        } else {
                          // Handle permission denied
                          Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT)
                              .show()
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
