package com.monkeyteam.chimpagne.ui.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun AdvancedLogisticsPanel(eventViewModel: EventViewModel) {
    val uiState by eventViewModel.uiState.collectAsState()

    var parkingText by remember { mutableStateOf(uiState.parkingSpaces.toString()) }
    var bedsText by remember { mutableStateOf(uiState.beds.toString()) }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            stringResource(id = R.string.event_creation_screen_logistics),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.testTag("logistics_title"))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(id = R.string.event_creation_screen_parking),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("parking_title"))
        OutlinedTextField(
            value = parkingText,
            onValueChange = {
                parkingText = it
                try {
                    eventViewModel.updateParkingSpaces(parkingText.toInt())
                } catch (_: Exception) {
                    eventViewModel.updateParkingSpaces(0)
                }
            },
            label = { Text(stringResource(id = R.string.event_creation_screen_number_parking)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().testTag("n_parking")
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(id = R.string.event_creation_screen_beds),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("beds_title"))
        OutlinedTextField(
            value = bedsText,
            onValueChange = {
                bedsText = it
                try {
                    eventViewModel.updateBeds(bedsText.toInt())
                } catch (_: Exception) {
                    eventViewModel.updateBeds(0)
                }
            },
            label = { Text(stringResource(id = R.string.event_creation_screen_number_beds)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().testTag("n_beds"))
    }

}
