package com.monkeyteam.chimpagne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.monkeyteam.chimpagne.model.database.Database
import com.monkeyteam.chimpagne.model.database.PRODUCTION_TABLES
import com.monkeyteam.chimpagne.ui.navigation.NavigationGraph
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.AccountViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val database = Database(PRODUCTION_TABLES, applicationContext)
    val accountViewModel: AccountViewModel by viewModels { AccountViewModelFactory(database) }

    setContent {
      ChimpagneTheme {
        val navController = rememberNavController()

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavigationGraph(
              navController = navController,
              database = database,
              accountViewModel = accountViewModel)
        }
      }
    }
  }
}
