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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun EventCreationScreen(initialPage: Int) {
    val pagerState = rememberPagerState(initialPage=initialPage) { 4 }
  val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
  Column {
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
      when (page) {
        0 -> FirstPanel()
        1 -> SecondPanel()
        2 -> ThirdPanel()
        3 -> FourthPanel()
      }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      if (pagerState.currentPage > 0) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            }) {
              Text("Previous")
            }
      } else {
        Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))
      }
      if (pagerState.currentPage < 3) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }) {
              Text("Next")
            }
      } else {
        Button(onClick = {
            Toast.makeText(context, "Event has been created !", Toast.LENGTH_SHORT).show()
        }) { Text("Create Event") }
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
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = descriptionText,
        onValueChange = { descriptionText = it },
        label = { Text("Description") },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 3)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = addressText,
        onValueChange = { addressText = it },
        label = { Text("Address") },
        modifier = Modifier.fillMaxWidth())
  }
}

@Composable
fun SecondPanel() {

  var tagsText by remember { mutableStateOf("") }

  val context = LocalContext.current
  Column(modifier = Modifier.padding(16.dp)) {
    Text("More event infos", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = tagsText,
        onValueChange = { tagsText = it },
        label = { Text("Tags (comma-separated)") },
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
          Toast.makeText(context, "This event has been made public !", Toast.LENGTH_SHORT).show()
        }) {
          Text("Make this event public")
        }
  }
}

@Composable
fun ThirdPanel() {
  Column(modifier = Modifier.padding(16.dp)) {
    Text("Groceries", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { /* Add groceries logic */}) { Text("Add groceries") }
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn {
      // Populate with groceries items
    }
  }
}

// Comment to make a new commit
@Composable
fun FourthPanel() {
  var parkingText by remember { mutableStateOf("") }
  var bedsText by remember { mutableStateOf("") }
  Column(modifier = Modifier.padding(16.dp)) {
    Text("Logistics", style = MaterialTheme.typography.headlineSmall)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Parking", style = MaterialTheme.typography.bodyMedium)

    OutlinedTextField(
        value = parkingText,
        onValueChange = { parkingText = it },
        label = { Text("Number of parking spaces") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth())

    Spacer(modifier = Modifier.height(16.dp))
    Text("Beds", style = MaterialTheme.typography.bodyMedium)
    OutlinedTextField(
        value = bedsText,
        onValueChange = { bedsText = it },
        label = { Text("Number of beds") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth())
  }
}
