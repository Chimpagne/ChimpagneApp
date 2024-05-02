package com.monkeyteam.chimpagne.ui.eventdetails.supplies

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun SuppliesScreen(
  eventViewModel: EventViewModel,
) {
  val uiState by eventViewModel.uiState.collectAsState()



  Button(onClick = {  }) {
    
  }
}