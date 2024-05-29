package com.monkeyteam.chimpagne.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@Composable
fun ChimpagneNavigationBar(navController: NavHostController, accountViewModel: AccountViewModel) {
  val navActions = NavigationActions(navController)
//  val accountViewModelState by accountViewModel.uiState.collectAsState()

  val currentRouteState by navController.currentBackStackEntryAsState()
  val currentRoute = currentRouteState?.destination?.route

//  if (accountViewModelState.currentUserAccount != null) {
  if (currentRoute != Route.LOGIN_SCREEN)
    NavigationBar {
      NAVIGATION_ITEMS.forEach { item->
        NavigationBarItem(
          selected = navController.currentDestination?.route == item.targetRoute,
          onClick = { navActions.navigateTo(item.targetRoute) },
          icon = {
            Icon(imageVector = item.icon, contentDescription = item.description)
          },
          label = { Text(item.description) }
        )
      }
//    }
  }
}

data class ChimpagneNavigationBarItem(
  val targetRoute: String,
  val icon: ImageVector,
  val description: String
)

val NAVIGATION_ITEMS = listOf(
  ChimpagneNavigationBarItem(
    targetRoute = Route.HOME_SCREEN,
    icon = Icons.Default.Home,
    description = "Home"
  ),
  ChimpagneNavigationBarItem(
    targetRoute = Route.FIND_AN_EVENT_SCREEN,
    icon = Icons.Default.Language,
    description = "Discover"
  ),
  ChimpagneNavigationBarItem(
    targetRoute = Route.MY_EVENTS_SCREEN,
    icon = Icons.Default.CalendarMonth,
    description = "My Events"
  )
)