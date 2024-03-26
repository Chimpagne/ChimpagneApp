package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCreationScreen() {
    val pagerState = rememberPagerState { 4 }
    val coroutineScope = rememberCoroutineScope()
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> FirstPanel()
                1 -> SecondPanel()
                2 -> ThirdPanel()
                3 -> FourthPanel()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))
            }
            if (pagerState.currentPage < 3) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                ) {
                    Text("Next")
                }
            } else {
                TextButton(onClick = { /* Create event logic */ }) {
                    Text("Create Event")
                }
            }
        }
    }
}

@Composable
fun FirstPanel() {
    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var addressText by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = titleText,
            onValueChange = { titleText = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = descriptionText,
            onValueChange = { descriptionText = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = addressText,
            onValueChange = { addressText = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SecondPanel() {

    var tagsText by remember { mutableStateOf("") }


    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        Headline(t = "More event infos")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = tagsText,
            onValueChange = { tagsText = it },
            label = { Text("Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Toast.makeText(context, "This event has been made public", Toast.LENGTH_SHORT)
                    .show()
            }
        ) {
            Text("Make this event public")
        }
    }
}

@Composable
fun ThirdPanel() {
    Column(modifier = Modifier.padding(16.dp)) {
        Headline(t = "Groceries")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Add groceries logic */ }) {
            Text("Add groceries")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            // Populate with groceries items
        }
    }
}

@Composable
fun Headline(t: String) {
    Text(t, style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun Subtitle(t: String) {

    Text(t, style = MaterialTheme.typography.bodyMedium)
}
// Comment to make a new commit
@Composable
fun FourthPanel() {
    Column(modifier = Modifier.padding(16.dp)) {
        Headline("Logistics")
        Spacer(modifier = Modifier.height(16.dp))

        Subtitle("Parking")

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Number of parking spaces") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Subtitle("Beds")
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Number of beds") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}