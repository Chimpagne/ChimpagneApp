package com.monkeyteam.chimpagne

import AccountSettings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.ChimpagneAccount
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

        val continueAsGuest: (Boolean) -> Unit = {
          navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
        }

        val loginToAccount:
            (
                id: String,
                onSuccess: (ChimpagneAccount?) -> Unit,
                onFailure: (Exception) -> Unit) -> Unit =
            { id, onSuccess, onFailure ->
              accountViewModel.loginToChimpagneAccount(id, onSuccess, onFailure)
            }

        val loginAccountNormal: (id: String) -> Unit = { id ->
          loginToAccount(
              id,
              { account ->
                if (account == null) {
                  Log.d("MainActivity", "Account is not in database")
                  navActions.clearAndNavigateTo(Route.ACCOUNT_CREATION_SCREEN)
                } else {
                  Log.d("MainActivity", "Account is in database")
                  navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
                }
              },
              { Log.e("MainActivity", "Failed to check if account is in database: $it") })
        }

        val loginAccountStart: (id: String) -> Unit = { id ->
          loginToAccount(
              id,
              { account ->
                if (account == null) {
                  Log.d("MainActivity", "Account is not in database")
                  navActions.clearAndNavigateTo(Route.LOGIN_SCREEN)
                } else {
                  Log.d("MainActivity", "Account is in database")
                  navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
                }
              },
              { Log.e("MainActivity", "Failed to check if account is in database: $it") })
        }

        val login: () -> Unit = {
          if (FirebaseAuth.getInstance().currentUser != null) {
            loginAccountStart(FirebaseAuth.getInstance().currentUser?.uid!!)
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
            composable(Route.LOADING_LOGIN) {
              SpinnerView()
              login()
            }
            composable(Route.LOGIN_SCREEN) {
              LoginScreen(
                  onSuccessfulLogin = { uid -> loginAccountNormal(uid) },
                  onContinueAsGuest = { continueAsGuest(false) })
            }
            composable(Route.ACCOUNT_CREATION_SCREEN) {
              val onSuccessACS = { navActions.clearAndNavigateTo(Route.HOME_SCREEN, true) }
              val onFailureACS = { navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true) }
              AccountCreation(
                  navObject = navActions,
                  accountViewModel = accountViewModel,
                  onSuccess = onSuccessACS,
                  onFailure = onFailureACS)
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
                                  backStackEntry.arguments?.getString("EventID"), database)),
                  accountViewModel = accountViewModel)
            }
            composable(Route.JOIN_EVENT_SCREEN) {
              val eventViewModel: EventViewModel =
                  viewModel(factory = EventViewModelFactory(null, database))
              val event = eventViewModel.buildChimpagneEvent()
              DetailScreenSheet(
                  event = event,
                  onJoinClick = {
                    navActions.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}/false")
                  })
            }
            composable(
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
                  if (FirebaseAuth.getInstance().currentUser != null) {
                    loginToAccount(
                        FirebaseAuth.getInstance().currentUser?.uid!!,
                        { Log.d("MainActivity", "Account is in database") },
                        { Log.e("MainActivity", "Failed to check if account is in database: $it") })
                  }

                  val EventID = it.arguments?.getString("EventID")
                  navActions.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${EventID}")
                }
          }
        }
      }
    }
  }
}
