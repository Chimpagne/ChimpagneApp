package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navObject: NavigationActions, accountViewModel: AccountViewModel) {

  val context = LocalContext.current
  val uiState by accountViewModel.uiState.collectAsState()
  var showPromptLogin by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("") },
            actions = {
              ProfileIcon(
                  uiState.currentUserProfilePicture,
                  onClick = {
                    if (!accountViewModel.isUserLoggedIn()) {
                      showPromptLogin = true
                    } else {
                      navObject.navigateTo(Route.ACCOUNT_SETTINGS_SCREEN)
                    }
                  })
            })
      }) { innerPadding ->
        if (showPromptLogin) {
          PromptLogin(context, navObject)
          showPromptLogin = false
        }
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              ChimpagneButton(
                  modifier = Modifier.testTag("open_events_button"),
                  onClick = {
                    if (!accountViewModel.isUserLoggedIn()) {
                      showPromptLogin = true
                    } else {
                      navObject.navigateTo(Route.MY_EVENTS_SCREEN)
                    }
                  },
                  text = stringResource(id = R.string.homescreen_my_events),
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
              Spacer(modifier = Modifier.height(16.dp))
              ChimpagneButton(
                  modifier = Modifier.testTag("discover_events_button"),
                  onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                  text = stringResource(R.string.homescreen_join_event),
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
              Spacer(modifier = Modifier.height(16.dp))
              ChimpagneButton(
                  modifier = Modifier.testTag("organize_event_button"),
                  onClick = {
                    if (!accountViewModel.isUserLoggedIn()) {
                      showPromptLogin = true
                    } else {
                      navObject.navigateTo(Route.EVENT_CREATION_SCREEN)
                    }
                  },
                  text = stringResource(R.string.homescreen_organize_event),
                  fontWeight = FontWeight.Bold,
                  fontSize = 30.sp)
            }
      }
}
