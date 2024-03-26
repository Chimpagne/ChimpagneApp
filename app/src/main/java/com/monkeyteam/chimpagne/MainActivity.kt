package com.monkeyteam.chimpagne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.ui.EventCreationScreen
import com.monkeyteam.chimpagne.ui.HomeScreen
import com.monkeyteam.chimpagne.ui.LoginScreen
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ChimpagneTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          val navActions = NavigationActions(navController)

          NavHost(navController = navController, startDestination = Route.LOGIN_SCREEN) {
            composable(Route.LOGIN_SCREEN) {
              LoginScreen {
                navController.navigate(Route.HOME_SCREEN) {
                  popUpTo(Route.LOGIN_SCREEN) { inclusive = true }
                }
                navController.graph.setStartDestination(Route.HOME_SCREEN)
              }
            }

            composable(Route.HOME_SCREEN) { HomeScreen(navObject = navActions) }
            composable(Route.EVENT_CREATION_SCREEN) { EventCreationScreen() }
          }
        }
      }
    }
  }
}
