package com.monkeyteam.chimpagne

import AccountSettings
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PUBLIC_TABLES
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.eventdetails.supplies.SuppliesScreen
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

        val loginToChimpagneAccount: (id: String) -> Unit = { id ->
          accountViewModel.loginToChimpagneAccount(
              id,
              { account ->
                if (account != null) {
                  Log.d("MainActivity", "Account is in database")
                  navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
                } else {
                  Log.d("MainActivity", "Account is not in database")
                  navActions.clearAndNavigateTo(Route.ACCOUNT_CREATION_SCREEN)
                }
              },
              { Log.e("MainActivity", "Failed to check if account is in database: $it") })
        }

        val logout: () -> Unit = {
          AuthUI.getInstance().signOut(this)
          accountViewModel.logoutFromChimpagneAccount()
          navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
        }

        // Determine the start destination based on the isAuthenticated state
        // Using null check to decide, assuming that null means the auth state is still being
        // determined
        val startDestination =
            if (FirebaseAuth.getInstance().currentUser != null) {
              loginToChimpagneAccount(FirebaseAuth.getInstance().currentUser?.uid!!)
              Route.LOADING
            } else {
              Route.LOGIN_SCREEN
            }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.LOGIN_SCREEN) {
              LoginScreen(
                  onSuccessfulLogin = { uid -> loginToChimpagneAccount(uid) },
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
            composable(Route.SUPPLIES + "/{EventID}") { backStackEntry ->
              val eventID = backStackEntry.arguments?.getString("EventID")
              SuppliesScreen(
                eventViewModel = EventViewModel(eventID, database, {}, {}),
                accountViewModel = accountViewModel
              )
            }
            composable(Route.MY_EVENTS_SCREEN) {
              val myEventsViewModel: MyEventsViewModel =
                  viewModel(factory = MyEventsViewModelFactory(database))
              myEventsViewModel.fetchMyEvents()
              MyEventsScreen(navObject = navActions, myEventsViewModel = myEventsViewModel)
            }
            composable(Route.VIEW_DETAIL_EVENT_SCREEN + "/{EventID}") { backStackEntry ->
              val eventViewModel: EventViewModel =
                  viewModel(
                      factory =
                          EventViewModelFactory(
                              backStackEntry.arguments?.getString("EventID"), database))
              eventViewModel.fetchEvent()
              ViewDetailEventScreen(navObject = navActions, eventViewModel = eventViewModel)
            }
          }
        }
      }
    }
  }
}
