package com.monkeyteam.chimpagne

import AccountSettingsScreen
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PUBLIC_TABLES
import com.monkeyteam.chimpagne.ui.EventScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.account.AccountUpdateScreen
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.event.details.supplies.SuppliesScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModelFactory

class MainActivity : ComponentActivity() {

  val database = Database(PUBLIC_TABLES)
  private val accountViewModel: AccountViewModel by viewModels { AccountViewModelFactory(database) }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ChimpagneTheme {
        val navController = rememberNavController()
        val navActions = NavigationActions(navController)

        val continueAsGuest: (Boolean) -> Unit = {
          navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
        }

        val login: (failureRoute: String) -> Unit = { successRoute ->
          if (FirebaseAuth.getInstance().currentUser != null) {
            accountViewModel.loginToChimpagneAccount(
                FirebaseAuth.getInstance().currentUser?.uid!!,
                { account ->
                  if (account == null) {
                    Log.e("MainActivity", "Account is not in database")
                    navActions.clearAndNavigateTo(successRoute)
                  } else {
                    Log.d("MainActivity", "Account is in database")
                    navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
                  }
                },
                { Log.e("MainActivity", "Failed to check if account is in database: $it") })
          } else {
            navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
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
              login(Route.LOGIN_SCREEN)
            }
            composable(Route.LOGIN_SCREEN) {
              LoginScreen(
                  onSuccessfulLogin = { uid -> login(Route.ACCOUNT_CREATION_SCREEN) },
                  onContinueAsGuest = { continueAsGuest(false) })
            }
            composable(Route.ACCOUNT_CREATION_SCREEN) {
              AccountUpdateScreen(
                  accountViewModel = accountViewModel,
                  onGoBack = { navActions.goBack() },
                  onAccountUpdated = { navActions.clearAndNavigateTo(Route.HOME_SCREEN, true) })
            }
            composable(Route.ACCOUNT_SETTINGS_SCREEN) {
              AccountSettingsScreen(
                  accountViewModel = accountViewModel,
                  onGoBack = { navActions.goBack() },
                  onLogout = logout,
                  onEditRequest = { navActions.navigateTo(Route.ACCOUNT_EDIT_SCREEN) })
            }
            composable(Route.ACCOUNT_EDIT_SCREEN) {
              AccountUpdateScreen(
                  accountViewModel = accountViewModel,
                  onAccountUpdated = { navActions.goBack() },
                  editMode = true,
                  onGoBack = { navActions.goBack() })
            }

            composable(Route.LOADING) { SpinnerView() }
            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions, accountViewModel) }
            composable(Route.FIND_AN_EVENT_SCREEN) {
              MainFindEventScreen(
                  navObject = navActions,
                  eventViewModel =
                      viewModel(factory = EventViewModel.EventViewModelFactory(null, database)),
                  findViewModel = viewModel(factory = FindEventsViewModelFactory(database)),
                  accountViewModel)
            }
            composable(Route.EVENT_CREATION_SCREEN) {
              EventCreationScreen(
                  navObject = navActions,
                  eventViewModel =
                      viewModel(factory = EventViewModel.EventViewModelFactory(null, database)))
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
            composable(
                route = Route.EVENT_SCREEN + "/{EventID}",
                deepLinks =
                    listOf(
                        navDeepLink {
                          uriPattern = getString(R.string.deep_link_url_event) + "{EventID}"
                          action = Intent.ACTION_VIEW
                        }),
                arguments = listOf(navArgument("EventID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val deeplinkHandled = intent?.action != Intent.ACTION_VIEW
                  if (!deeplinkHandled) {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                      accountViewModel.loginToChimpagneAccount(
                          FirebaseAuth.getInstance().currentUser?.uid!!, {}, {})
                    }
                  }
                  EventScreen(
                      navObject = navActions,
                      eventViewModel =
                          viewModel(
                              factory =
                                  EventViewModel.EventViewModelFactory(
                                      backStackEntry.arguments?.getString("EventID"), database)),
                      accountViewModel = accountViewModel)
                }
            composable(Route.MANAGE_STAFF_SCREEN + "/{EventID}") { backStackEntry ->
              val eventViewModel: EventViewModel =
                  viewModel(
                      factory =
                          EventViewModel.EventViewModelFactory(
                              backStackEntry.arguments?.getString("EventID"), database))
              eventViewModel.fetchEvent({
                accountViewModel.fetchAccounts(
                    listOf(eventViewModel.uiState.value.ownerId) +
                        eventViewModel.uiState.value.staffs.keys.toList() +
                        eventViewModel.uiState.value.guests.keys.toList())
              })
              ManageStaffScreen(
                  navObject = navActions,
                  eventViewModel = eventViewModel,
                  accountViewModel = accountViewModel)
            }
            composable(Route.SUPPLIES_SCREEN + "/{EventID}") { backStackEntry ->
              val eventViewModel: EventViewModel =
                  viewModel(
                      factory =
                          EventViewModel.EventViewModelFactory(
                              backStackEntry.arguments?.getString("EventID"), database))
              eventViewModel.fetchEvent({
                accountViewModel.fetchAccounts(
                    listOf(eventViewModel.uiState.value.ownerId) +
                        eventViewModel.uiState.value.staffs.keys.toList() +
                        eventViewModel.uiState.value.guests.keys.toList())
              })
              SuppliesScreen(
                  navObject = navActions,
                  eventViewModel = eventViewModel,
                  accountViewModel = accountViewModel)
            }
          }
        }
      }
    }
  }
}
