package com.monkeyteam.chimpagne.ui.navigation

import androidx.navigation.NavHostController

object Route {
  const val EDIT_EVENT_SCREEN = "EDIT_EVENT_SCREEN"
  const val LOGIN_SCREEN = "Login"
  const val ACCOUNT_CREATION_SCREEN = "AccountCreation"
  const val ACCOUNT_SETTINGS_SCREEN = "AccountSettings"
  const val ACCOUNT_EDIT_SCREEN = "AccountEdit"
  const val HOME_SCREEN = "Home"
  const val FIND_AN_EVENT_SCREEN = "FindAnEvent"
  const val LOADING = "loading"
  const val EVENT_CREATION_SCREEN = "EVENT_CREATION_SCREEN"
  const val MY_EVENTS_SCREEN = "myEvents"
  const val EVENT_SCREEN = "viewDetailEventScreen"
  const val ONLINE_EVENT_VIEW = "onlineEventView"
  const val LOADING_LOGIN = "loadingLogin"
  const val MANAGE_STAFF_SCREEN = "manageStaffScreen"
  const val SUPPLIES_SCREEN = "SuppliesScreen"
}

class NavigationActions(private val navController: NavHostController) {
  fun navigateTo(route: String) {
    navController.navigate(route) {
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
    }
  }

  fun goBack() {
    navController.navigateUp()
  }

  fun clearAndNavigateTo(route: String, setAsStartDestination: Boolean = false) {
    navController.navigate(route) {
      popUpTo(0) { inclusive = true }
      launchSingleTop = true
    }
    if (setAsStartDestination) navController.graph.setStartDestination(route)
  }

  fun popBackStack() {
    navController.popBackStack()
  }
}
