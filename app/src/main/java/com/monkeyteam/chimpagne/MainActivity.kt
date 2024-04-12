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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.monkeyteam.chimpagne.ui.AccountEdit
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.MainFindEventScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.AccountCreation
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val accountViewModel: AccountViewModel by viewModels()

    setContent {
      ChimpagneTheme {
        val navController = rememberNavController()
        val navActions = NavigationActions(navController)

        var isAuthenticated by remember {
          mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
        }

        val loggedToAChimpagneAccount by accountViewModel.loggedToAChimpagneAccount.collectAsState()

        val logout: () -> Unit = {
          AuthUI.getInstance().signOut(this)

          accountViewModel.logoutFromChimpagneAccount()

          navActions.clearAndNavigateTo(Route.LOGIN_SCREEN, true)
        }

        val loginToChimpagneAccount: (email: String) -> Unit = { email ->
          accountViewModel.loginToChimpagneAccount(
              email,
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

        // Determine the start destination based on the isAuthenticated state
        // Using null check to decide, assuming that null means the auth state is still being
        // determined
        val startDestination =
            when (isAuthenticated) {
              true ->
                  when (loggedToAChimpagneAccount) {
                    true -> Route.HOME_SCREEN
                    false -> Route.ACCOUNT_CREATION_SCREEN
                    else -> {
                      loginToChimpagneAccount(FirebaseAuth.getInstance().currentUser?.email!!)
                      Route.LOADING
                    }
                  }
              false -> Route.LOGIN_SCREEN
            }

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.LOGIN_SCREEN) { LoginScreen { email -> loginToChimpagneAccount(email) } }
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

            composable("loading") { SpinnerView() }
            composable(Route.LOADING) { SpinnerView() }
            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions) }
            composable(Route.FIND_AN_EVENT_SCREEN) { MainFindEventScreen(navObject = navActions) }
            composable(Route.EVENT_CREATION_SCREEN) { EventCreationScreen(navObject = navActions) }
          }
        }
      }
    }
  }
}
