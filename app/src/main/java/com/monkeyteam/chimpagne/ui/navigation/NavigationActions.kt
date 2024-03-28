package com.monkeyteam.chimpagne.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val LOGIN_SCREEN = "Login"
  const val ACCOUNT_CREATION_SCREEN = "AccountCreation"
  const val HOME_SCREEN = "Home"
  const val FIND_AN_EVENT_SCREEN = "FindAnEvent"
  const val LOADING = "loading"
}

class NavigationActions(private val navController: NavHostController) {
  fun navigateTo(route: String) {
    navController.navigate(route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }

  fun goBack() {
    navController.navigateUp()
  }
}
