package com.monkeyteam.chimpagne.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventManager
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.location.Location
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  val context = LocalContext.current
  val uiState by accountViewModel.uiState.collectAsState()
  var showPromptLogin by remember { mutableStateOf(false) }

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

                val database = Database()
                val findViewModel = FindEventsViewModel(database)
                var eventsNearMe = mutableListOf<ChimpagneEvent>()

            fun getClosestNEvent(li: List<ChimpagneEvent>, n: Int, myLocation: Location): List<ChimpagneEvent> {
                if (li.size <= n) {
                    return li
                }

                val sortedEvents = eventsNearMe.sortedBy { event ->
                    val eventLocation = event.location
                    myLocation.distanceTo(eventLocation)
                }

                return sortedEvents.take(n)
            }
            val fusedLocationProviderClient = remember {
                LocationServices.getFusedLocationProviderClient(context)
            }
            val locationPermissionRequest =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                            if (ActivityCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                                ) !=
                                PackageManager.PERMISSION_GRANTED
                            ) {

                                return@rememberLauncherForActivityResult
                            }

                            fusedLocationProviderClient
                                .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
                                .addOnSuccessListener { location ->
                                    location?.let {
                                        findViewModel.updateSelectedLocation(
                                            Location("mylocation", it.latitude, it.longitude)
                                        )

                                        findViewModel.fetchAroundLocation(onSuccess = {
                                            findViewModel.uiState.value.events.forEach { t, u ->
                                                eventsNearMe.add(
                                                    u
                                                )
                                            }

                                            findViewModel.uiState.value.selectedLocation?.let {
                                                val closestEvents = getClosestNEvent(
                                                    eventsNearMe, 4,
                                                    it
                                                )

                                                closestEvents.forEach { println(it) }
                                            }

                                        }, onFailure = {
                                            println("couscous")
                                        })

                                    }
                                }
                                .addOnFailureListener { }
                        }

                        else -> println("location denied")
                    }
                }


            locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

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
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
              Spacer(modifier = Modifier.height(16.dp))
              ChimpagneButton(
                  modifier = Modifier.testTag("discover_events_button"),
                  onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                  text = stringResource(R.string.homescreen_join_event),
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
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
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
            }
      }
}
