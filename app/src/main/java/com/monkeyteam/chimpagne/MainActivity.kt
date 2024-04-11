package com.monkeyteam.chimpagne

import AccountSettings
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.FindAnEventScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.ui.viewmodel.AccountViewModel
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
        var accountViewModel = AccountViewModel("test@gmail.com")

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
                Database.instance.accountManager.isInDatabase(
                    FirebaseAuth.getInstance().currentUser?.email!!,
                    onSuccess = {
                      if (it) {
                        Log.d("MainActivity", "Account is in database")
                        navController.navigate(Route.HOME_SCREEN) {
                          accountViewModel =
                              FirebaseAuth.getInstance().currentUser?.email?.let {
                                AccountViewModel(it)
                              }!!
                          popUpTo(Route.LOGIN_SCREEN) { inclusive = true }
                        }
                        navController.graph.setStartDestination(Route.HOME_SCREEN)
                      } else {
                        Log.d("MainActivity", "Account is not in database")
                        navController.navigate(Route.ACCOUNT_CREATION_SCREEN) {
                          popUpTo(Route.LOGIN_SCREEN) { inclusive = true }
                        }
                      }
                    },
                    onFailure = {
                      Log.e("MainActivity", "Failed to check if account is in database: $it")
                    })
              }
            }
            composable(Route.ACCOUNT_CREATION_SCREEN) {
              AccountCreation(navObject = navActions, accountViewModel = AccountViewModel(FirebaseAuth.getInstance().currentUser?.email!!))
            }
            composable(Route.ACCOUNT_SETTINGS_SCREEN) {
              AccountSettings(navObject = navActions, accountViewModel = accountViewModel)
            }
            composable(Route.ACCOUNT_EDIT_SCREEN) {
              AccountEdit(navObject = navActions, accountViewModel = accountViewModel)
            }

            composable("loading") { SpinnerView() }
            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions) }
            composable(Route.FIND_AN_EVENT_SCREEN) { FindAnEventScreen(navObject = navActions) }
            composable(Route.EVENT_CREATION_SCREEN) { EventCreationScreen(navObject = navActions) }
          }
        }
      }
    }
  }
}
