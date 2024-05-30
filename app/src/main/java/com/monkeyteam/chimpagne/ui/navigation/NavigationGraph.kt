package com.monkeyteam.chimpagne.ui.navigation

import AccountSettingsScreen
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.monkeyteam.chimpagne.ui.event.polls.PollsAndVotingScreen
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModelFactory
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModel
import com.monkeyteam.chimpagne.viewmodels.MyEventsViewModelFactory

/**
 * The app starts at the route LOADING NavigationGraph is also responsible for the dynamic
 * navigation between screens when the user is logged in (or not)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    database: Database,
    accountViewModel: AccountViewModel,
) {
  val context = LocalContext.current as Activity
  val navActions = NavigationActions(navController)

  val onLogin: () -> Unit = {
    if (FirebaseAuth.getInstance().currentUser != null) {
      accountViewModel.loginToChimpagneAccount(
          FirebaseAuth.getInstance().currentUser?.uid!!,
          { account ->
            if (account == null) {
              Log.e("LoginRoute", "Account is not in database")
              navActions.clearAndNavigateTo(Route.ACCOUNT_CREATION_SCREEN)
            } else {
              Log.d("LoginRoute", "Account is in database")
              navActions.clearAndNavigateTo(Route.HOME_SCREEN, true)
            }
          },
          { Log.e("LoginRoute", "Failed to check if account is in database: $it") })
    } else {
      navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
    }
  }

  val onLogout: () -> Unit = {
    AuthUI.getInstance().signOut(context).addOnCompleteListener {
      accountViewModel.logoutFromChimpagneAccount()
      navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
    }
  }

  val onContinueAsGuest: () -> Unit = { navActions.clearAndNavigateTo(Route.HOME_SCREEN, true) }

  LaunchedEffect(Unit) { onLogin() }

  NavHost(navController = navController, startDestination = Route.LOADING_LOGIN) {
    composable(Route.LOADING_LOGIN) { SpinnerView() }

    composable(Route.LOGIN_SCREEN) {
      LoginScreen(onSuccessfulLogin = { onLogin() }, onContinueAsGuest = onContinueAsGuest)
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
          onLogout = onLogout,
          onEditRequest = { navActions.navigateTo(Route.ACCOUNT_EDIT_SCREEN) })
    }

    composable(Route.ACCOUNT_EDIT_SCREEN) {
      AccountUpdateScreen(
          accountViewModel = accountViewModel,
          onAccountUpdated = { navActions.goBack() },
          editMode = true,
          onGoBack = { navActions.goBack() })
    }

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
                  uriPattern = getString(context, R.string.deep_link_url_event) + "{EventID}"
                  action = Intent.ACTION_VIEW
                }),
        arguments = listOf(navArgument("EventID") { type = NavType.StringType })) { backStackEntry
          ->
          val deeplinkHandled = context.intent?.action != Intent.ACTION_VIEW
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
        accountViewModel.fetchAccounts(eventViewModel.buildChimpagneEvent().userSet().toList())
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
        accountViewModel.fetchAccounts(eventViewModel.buildChimpagneEvent().userSet().toList())
      })
      SuppliesScreen(
          navObject = navActions,
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel)
    }

    composable(Route.POLLS_SCREEN + "/{EventID}") { backStackEntry ->
      val eventViewModel: EventViewModel =
          viewModel(
              factory =
                  EventViewModel.EventViewModelFactory(
                      backStackEntry.arguments?.getString("EventID"), database))
      eventViewModel.fetchEvent()
      PollsAndVotingScreen(
          eventViewModel = eventViewModel,
          accountViewModel = accountViewModel,
          onGoBack = { navActions.goBack() })
    }
  }
}
