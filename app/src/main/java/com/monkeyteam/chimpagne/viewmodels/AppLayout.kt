package com.monkeyteam.chimpagne.viewmodels

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.ui.navigation.ChimpagneNavigationBar
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph

@Composable
fun AppLayout(
  navController: NavHostController,
  database: Database,
  accountViewModel: AccountViewModel,
  ) {
  Scaffold(
    bottomBar = {
//      ChimpagneNavigationBar(navController = navController, accountViewModel = accountViewModel)
    }
  ) { paddingValues ->
    NavigationGraph(navController = navController, database = database, accountViewModel = accountViewModel, modifier = Modifier.padding(paddingValues))
  }
}