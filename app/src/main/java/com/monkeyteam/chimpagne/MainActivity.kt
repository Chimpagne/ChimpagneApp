package com.monkeyteam.chimpagne

import AccountSettings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PUBLIC_TABLES
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.DetailScreenSheet
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModelFactory

class MainActivity : ComponentActivity() {

  val database = Database(PUBLIC_TABLES)
  private val accountViewModel: AccountViewModel by viewModels { AccountViewModelFactory(database) }

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ChimpagneTheme {
        val navController = rememberNavController()
        val navActions = NavigationActions(navController)

        val continueAsGuest: () -> Unit = { navActions.clearAndNavigateTo(Route.HOME_SCREEN, true) }

        val loginToChimpagneAccount: (id: String, isDeepLink: Boolean) -> Unit = { id, isDeepLink ->
          accountViewModel.loginToChimpagneAccount(
              id,
              { account ->
                if (account != null) {
                  Log.d("MainActivity", "Account is in database")
                  if (!isDeepLink) {
                    navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
                  }
                } else {
                  Log.d("MainActivity", "Account is not in database")
                  navActions.clearAndNavigateTo(Route.ACCOUNT_CREATION_SCREEN)
                }
              },
              { Log.e("MainActivity", "Failed to check if account is in database: $it") })
        }

        val login: (isDeepLink: Boolean) -> Unit = { bool ->
          if (FirebaseAuth.getInstance().currentUser != null) {
            loginToChimpagneAccount(FirebaseAuth.getInstance().currentUser?.uid!!, bool)
          } else {
            navActions.navigateTo(Route.LOGIN_SCREEN)
          }
        }

        val logout: () -> Unit = {
          AuthUI.getInstance().signOut(this)
          accountViewModel.logoutFromChimpagneAccount()
          navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
        }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(navController = navController, startDestination = Route.LOADING_LOGIN) {
              composable(Route.LOADING_LOGIN){
                  SpinnerView()
                  login(false)
              }
            composable(Route.LOGIN_SCREEN) {
              LoginScreen(
                  onSuccessfulLogin = { uid -> loginToChimpagneAccount(uid, false) },
                  onContinueAsGuest = continueAsGuest)
            }
            composable(Route.ACCOUNT_CREATION_SCREEN) {
              AccountCreation(navObject = navActions, accountViewModel = accountViewModel)
            }
            composable(Route.ACCOUNT_SETTINGS_SCREEN) {
              AccountSettings(
                  navObject = navActions, accountViewModel = accountViewModel, logout = logout)
            }
            composable(Route.ACCOUNT_EDIT_SCREEN) {
              AccountEdit(navObject = navActions, accountViewModel = accountViewModel)
            }

            composable(Route.LOADING) { SpinnerView() }
            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions, accountViewModel) }
            composable(Route.FIND_AN_EVENT_SCREEN) {
              MainFindEventScreen(
                  navObject = navActions,
                  findViewModel = viewModel(factory = FindEventsViewModelFactory(database)),
                  accountViewModel)
            }
            composable(Route.EVENT_CREATION_SCREEN) {
              EventCreationScreen(
                  navObject = navActions,
                  eventViewModel = viewModel(factory = EventViewModelFactory(null, database)))
            }
            composable(Route.EDIT_EVENT_SCREEN + "/{EventID}") { backStackEntry ->
              val eventID = backStackEntry.arguments?.getString("EventID")
              EditEventScreen(
                  initialPage = 0,
                  navObject = navActions,
                  eventViewModel = EventViewModel(eventID, database, {}, {}))
            }
            composable(Route.MY_EVENTS_SCREEN) {
              val myEventsViewModel: MyEventsViewModel =
                  viewModel(factory = MyEventsViewModelFactory(database))
              myEventsViewModel.fetchMyEvents()
              MyEventsScreen(navObject = navActions, myEventsViewModel = myEventsViewModel)
            }
            composable(Route.VIEW_DETAIL_EVENT_SCREEN + "/{EventID}") { backStackEntry ->
              ViewDetailEventScreen(
                  navObject = navActions,
                  eventViewModel =
                      viewModel(
                          factory =
                              EventViewModelFactory(
                                  backStackEntry.arguments?.getString("EventID"), database)))
            }
            composable(Route.JOIN_EVENT_SCREEN) {
              val eventViewModel: EventViewModel =
                  viewModel(factory = EventViewModelFactory(null, database))
              /*

              For you Gregory :) Use this not the one above

                              val eventViewModel =
                                  EventViewModel(
                                      possibleEventID,
                                      Database(PUBLIC_TABLES),
                                      onFailure = {
                                          Toast.makeText(context, "Event no longer available", Toast.LENGTH_SHORT)
                                              .show()
                                          navActions.clearAndNavigateTo(Route.HOME_SCREEN)
                                      })

               */
              val event = eventViewModel.buildChimpagneEvent()
              DetailScreenSheet(
                  event = event,
                  onJoinClick = {
                    navActions.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}/false")
                  })
            }
            composable(
                // The deep link route
                route = Route.ONLINE_EVENT_VIEW,
                deepLinks =
                    listOf(
                        navDeepLink {
                          uriPattern = "https://www.manigo.ch/events/?uid={EventID}"
                          action = Intent.ACTION_VIEW
                        }),
                arguments =
                    listOf(
                        navArgument("EventID") { type = NavType.StringType },
                    )) {
                  // Check if user is logged in
                  login(true)
                  val possibleEventID = it.arguments?.getString("EventID")
                  val context = LocalContext.current
                  // Check if event exists, before forwarding it to the event detail screen
                  EventViewModel(
                      possibleEventID,
                      Database(PUBLIC_TABLES),
                      onSuccess = {
                        navActions.navigateTo(
                            Route.VIEW_DETAIL_EVENT_SCREEN + "/${possibleEventID}/false")
                      },
                      onFailure = {
                        Toast.makeText(context, "Event no longer available", Toast.LENGTH_SHORT)
                            .show()
                        navActions.clearAndNavigateTo(Route.HOME_SCREEN)
                      })
                }
          }
        }
      }
    }
  }
}
