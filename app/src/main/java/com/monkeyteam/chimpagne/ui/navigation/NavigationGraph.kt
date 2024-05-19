package com.monkeyteam.chimpagne.ui.navigation

import AccountSettings
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.DetailScreenSheet
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.ManageStaffScreen
import com.monkeyteam.chimpagne.ui.MyEventsScreen
import com.monkeyteam.chimpagne.ui.ViewDetailEventScreen
import com.monkeyteam.chimpagne.ui.event.EditEventScreen
import com.monkeyteam.chimpagne.ui.event.EventCreationScreen
import com.monkeyteam.chimpagne.ui.event.details.supplies.SuppliesScreen
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationGraph(navController: NavHostController, accountViewModel: AccountViewModel, database: Database) {
  val navActions = NavigationActions(navController)
  val context = LocalContext.current

  val login: () -> Unit = {
    if (FirebaseAuth.getInstance().currentUser != null) {
      accountViewModel.loginToChimpagneAccount(
        FirebaseAuth.getInstance().currentUser?.uid!!,
        { account ->
          if (account == null) {
            Log.e("MainActivity", "Account is not in database")
            navActions.clearAndNavigateTo(Route.ACCOUNT_CREATION_SCREEN)
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
    AuthUI.getInstance().signOut(context)
    accountViewModel.logoutFromChimpagneAccount()
    navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
  }

  NavHost(navController = navController, startDestination = Route.LOADING_APP) {
    composable(Route.LOADING_APP) {
      SpinnerView()
      login()
    }

    composable(Route.LOGIN_SCREEN) {
      LoginScreen(
        onSuccessfulLogin = { login() },
        onContinueAsGuest = {
          navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
        })
    }

    composable(Route.ACCOUNT_CREATION_SCREEN) {
      AccountCreation(
        navObject = navActions,
        accountViewModel = accountViewModel,
        onSuccess = {
          navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
        },
        onFailure = {
          navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
        })
    }

    composable(Route.ACCOUNT_SETTINGS_SCREEN) {
      AccountSettings(
        navObject = navActions, accountViewModel = accountViewModel, logout = logout)
    }
    composable(Route.ACCOUNT_EDIT_SCREEN) {
      AccountEdit(navObject = navActions, accountViewModel = accountViewModel)
    }

    // WILL BE REMOVED IN THE FUTURE
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
      route = Route.VIEW_DETAIL_EVENT_SCREEN + "/{EventID}",
      deepLinks =
      listOf(
        navDeepLink {
          uriPattern = getString(context, R.string.deep_link_url_event) + "{EventID}"
          action = Intent.ACTION_VIEW
        }),
      arguments =
      listOf(
        navArgument("EventID") { type = NavType.StringType },
      )) { backStackEntry ->
      val deeplinkHandled = (context as Activity).intent?.action != Intent.ACTION_VIEW
      if (!deeplinkHandled) {
        if (FirebaseAuth.getInstance().currentUser != null) {
          accountViewModel.loginToChimpagneAccount(
            FirebaseAuth.getInstance().currentUser?.uid!!, {}, {})
        }
      }
      ViewDetailEventScreen(
        navObject = navActions,
        eventViewModel =
        viewModel(
          factory =
          EventViewModel.EventViewModelFactory(
            backStackEntry.arguments?.getString("EventID"), database)),
        accountViewModel = accountViewModel)
    }

    composable(Route.JOIN_EVENT_SCREEN) {
      val eventViewModel: EventViewModel =
        viewModel(factory = EventViewModel.EventViewModelFactory(null, database))
      val event = eventViewModel.buildChimpagneEvent()
      DetailScreenSheet(
        event = event,
        onJoinClick = {
          navActions.navigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${event.id}/false")
        })
    }

    composable(Route.MANAGE_STAFF_SCREEN + "/{EventID}") { backStackEntry ->
      val eventViewModel: EventViewModel =
        viewModel(
          factory =
          EventViewModel.EventViewModelFactory(
            backStackEntry.arguments?.getString("EventID"), database))
      eventViewModel.fetchEvent({
        accountViewModel.fetchAccounts(
          eventViewModel.buildChimpagneEvent().userSet().toList()
        )
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
          eventViewModel.buildChimpagneEvent().userSet().toList()
        )
      })
      SuppliesScreen(
        navObject = navActions,
        eventViewModel = eventViewModel,
        accountViewModel = accountViewModel)
    }
  }
}
