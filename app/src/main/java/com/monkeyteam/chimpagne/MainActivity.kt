package com.monkeyteam.chimpagne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.FindAnEventScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val authViewModel: AuthViewModel by viewModels()

    setContent {
      ChimpagneTheme {
        // A surface container using the 'background' color from the them
        val navController = rememberNavController()
        val navActions = NavigationActions(navController)

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = null)

          // The LaunchedEffect should react to changes in the isAuthenticated state
          LaunchedEffect(isAuthenticated) {
            isAuthenticated?.let {
              navController.navigate(if (it) Route.HOME_SCREEN else Route.LOGIN_SCREEN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
              }
            }
          }

          // Determine the start destination based on the isAuthenticated state
          // Using null check to decide, assuming that null means the auth state is still being
          // determined
          val startDestination =
              when (isAuthenticated) {
                true -> Route.HOME_SCREEN
                false -> Route.LOGIN_SCREEN
                else -> Route.LOADING
              }

          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.LOGIN_SCREEN) {
              LoginScreen {
                navController.navigate(Route.HOME_SCREEN) {
                  popUpTo(Route.LOGIN_SCREEN) { inclusive = true }
                }
                navController.graph.setStartDestination(Route.HOME_SCREEN)
              }
            }

            composable("loading") { SpinnerView() }
            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions) }
            composable(Route.FIND_AN_EVENT_SCREEN) { FindAnEventScreen(navObject = navActions) }
            composable(Route.EVENT_CREATION_SCREEN) { EventCreationScreen(0) }
          }
        }
      }
    }
  }
}
