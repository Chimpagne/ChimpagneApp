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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.User
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navObject: NavigationActions) {
  // Dummy user Data
  val dummyUser = User("0", "Lora", null)

  ChimpagneTheme {
    Scaffold(
        topBar = {
          TopAppBar(title = { Text("") }, actions = { ProfileIcon(user = dummyUser) })
        }) { innerPadding ->
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .background(MaterialTheme.colorScheme.background)
                      .padding(innerPadding),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center) {
                ChimpagneButton(
                    onClick = { /*TODO*/},
                    text = {
                      Text(
                          text =  stringResource(id = R.string.homescreen_my_events),
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                ChimpagneButton(
                    onClick = { navObject.navigateTo(Route.FIND_AN_EVENT_SCREEN) },
                    text = {
                      Text(
                          text = stringResource(R.string.homescreen_join_event),
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                ChimpagneButton(
                    onClick = { navObject.navigateTo(Route.EVENT_CREATION_SCREEN) },
                    text = {
                      Text(
                          text = stringResource(R.string.homescreen_organize_event),
                          fontFamily = ChimpagneFontFamily,
                          fontWeight = FontWeight.Bold,
                          fontSize = 30.sp)
                    })
              }
        }
  }
}
