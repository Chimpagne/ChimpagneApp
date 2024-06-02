package com.monkeyteam.chimpagne.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.utilities.promptLogin
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@Composable
fun ChimpagneNavigationBar(navController: NavHostController, accountViewModel: AccountViewModel) {
  val context = LocalContext.current
  val navActions = NavigationActions(navController)
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  val currentRouteState by navController.currentBackStackEntryAsState()
  val currentRoute = currentRouteState?.destination?.route

  val navigationItems =
      listOf(
          ChimpagneNavigationBarItem(
              targetRoute = Route.HOME_SCREEN,
              icon = Icons.Default.Home,
              description = stringResource(id = R.string.navbar_home),
              testTag = "home_button"),
          ChimpagneNavigationBarItem(
              targetRoute = Route.FIND_AN_EVENT_SCREEN,
              icon = Icons.Default.Language,
              description = stringResource(id = R.string.navbar_discover),
              testTag = "discover_events_button"),
          ChimpagneNavigationBarItem(
              targetRoute = Route.MY_EVENTS_SCREEN,
              icon = Icons.Default.CalendarMonth,
              description = stringResource(id = R.string.narbar_my_events),
              testTag = "open_events_button",
              accountRequired = true))

  NavigationBar(
      containerColor =
          if (BLACKLISTED_ROUTES.contains(currentRoute)) Color.Transparent
          else NavigationBarDefaults.containerColor) {
        if (BLACKLISTED_ROUTES.contains(currentRoute)) {
          return@NavigationBar
        }
        navigationItems.forEach { item ->
          NavigationBarItem(
              selected = navController.currentDestination?.route == item.targetRoute,
              onClick = {
                if (item.accountRequired && accountViewModelState.currentUserUID == null) {
                  promptLogin(context, navActions)
                } else {
                  navActions.navigateTo(item.targetRoute)
                }
              },
              icon = { Icon(imageVector = item.icon, contentDescription = item.description) },
              label = { Text(item.description) },
              modifier = Modifier.testTag(item.testTag))
        }
      }
}

val BLACKLISTED_ROUTES =
    listOf(Route.LOADING_LOGIN, Route.LOGIN_SCREEN, Route.ACCOUNT_CREATION_SCREEN)

data class ChimpagneNavigationBarItem(
    val targetRoute: String,
    val icon: ImageVector,
    val description: String,
    val testTag: String,
    val accountRequired: Boolean = false
)
