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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.User
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navObject: NavigationActions) {
  // Dummy user Data
  val dummyUser = User("0", "Lora", null)

  ChimpagneTheme {
    Scaffold(
        topBar = {
          TopAppBar(
              title = { Text("") },
              navigationIcon = {
                GoBackButton(navigationActions = navObject, modifier = Modifier.testTag("Go Back"))
              },
              actions = {
                ProfileIcon(user = dummyUser, modifier = Modifier.testTag("ProfileIcon"))
              })
        }) { innerPadding ->
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .background(MaterialTheme.colorScheme.background)
                      .padding(innerPadding),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                ChimpagneButton(
                    modifier = Modifier.testTag("MyEventsButton"),
                    onClick = { /*TODO*/},
                    text = {
                      Text(
                          text = "MY EVENTS",
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                ChimpagneButton(
<<<<<<< HEAD
                    modifier = Modifier.testTag("JoinEventButton"),
=======
>>>>>>> main
                    onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                    text = {
                      Text(
                          text = "JOIN AN EVENT",
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                ChimpagneButton(
                    modifier = Modifier.testTag("OrganizeEventButton"),
                    onClick = { /*TODO*/},
                    text = {
                      Text(
                          text = "ORGANIZE AN EVENT",
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
              }
        }
  }
}
