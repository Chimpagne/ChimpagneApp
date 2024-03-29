package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import kotlinx.coroutines.launch

object FindEventScreens {
  const val FORM = 0
  const val MAP = 1
}

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun MainFindEventScreen(navObject: NavigationActions) {
  val pagerState = rememberPagerState { 2 }
  val coroutineScope = rememberCoroutineScope()

  val goToForm: () -> Unit = {
    coroutineScope.launch { pagerState.animateScrollToPage(FindEventScreens.FORM) }
  }

  val goToMap: () -> Unit = {
    coroutineScope.launch { pagerState.animateScrollToPage(FindEventScreens.MAP) }
  }

  ChimpagneTheme {
    HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
      when (page) {
        FindEventScreens.FORM -> FindEventFormScreen(navObject, goToMap)
        FindEventScreens.MAP -> FindEventMapScreen(goToForm)
      }
    }
  }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventFormScreen(navObject: NavigationActions, onSearchClick: () -> Unit) {
  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Find an event") },
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            })
      },
      bottomBar = {
        Button(
            onClick = { onSearchClick() },
            modifier =
                Modifier.fillMaxWidth().padding(8.dp).height(56.dp), // Typical height for buttons
            shape = MaterialTheme.shapes.medium) {
              Icon(Icons.Rounded.Search, contentDescription = "Search")
              Spacer(Modifier.width(8.dp))
              Text("Search")
            }
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          Column(
              modifier =
                  Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Enter Keywords",
                    modifier = Modifier.align(Alignment.Start).padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                // Keyword search text field
                TextField(
                    value = "", // This should be a state you're managing
                    onValueChange = {},
                    placeholder = { Text("Search...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Add Tags to your Search",
                    modifier = Modifier.align(Alignment.Start).padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                // Tags text field
                TextField(
                    value = "", // This should be a state you're managing
                    onValueChange = {},
                    placeholder = { Text("Enter a tag...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
                Spacer(Modifier.height(8.dp))

                // Tags row
                Row(modifier = Modifier.padding(8.dp)) {
                  Chip(label = "Tags 1")
                  Spacer(Modifier.width(8.dp))
                  Chip(label = "Tags 2")
                  Spacer(Modifier.width(8.dp))
                  Chip(label = "Tags 1")
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Select the date of the event",
                    modifier = Modifier.align(Alignment.Start).padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
                // Date picker field
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Rounded.CalendarToday, contentDescription = "Select date")
                  Spacer(Modifier.width(8.dp))
                  Text("5 may 2024") // This should be a state you're managing
                }

                Spacer(Modifier.height(16.dp))
              }
        }
      }
}

@Composable
fun Chip(label: String) {
  Surface(
      shape = MaterialTheme.shapes.small,
      color = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.onSurfaceVariant) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
              Checkbox(
                  checked = true, // This should be a state you're managing
                  onCheckedChange = null // Provide an actual lambda to handle check changes
                  )
              Spacer(Modifier.width(4.dp))
              Text(text = label)
            }
      }
}

@ExperimentalMaterial3Api
@Composable
fun FindEventMapScreen(onBackIconClicked: () -> Unit) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()

  val systemUiPadding = WindowInsets.systemBars.asPaddingValues()

  BottomSheetScaffold(
      sheetContent = {
        Box(Modifier.fillMaxWidth().height(128.dp), contentAlignment = Alignment.Center) {
          Text("Swipe up to expand sheet")
        }
        Column(
            Modifier.fillMaxWidth().padding(64.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Sheet content")
              Spacer(Modifier.height(20.dp))
              Button(
                  onClick = { scope.launch { scaffoldState.bottomSheetState.partialExpand() } }) {
                    Text("Click to collapse sheet")
                  }
            }
      },
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp) { innerPadding ->
        Box(modifier = Modifier.padding(top = systemUiPadding.calculateTopPadding())) {
          MapContainer()
          Column {
            FindEventSearchBar("Before Balelec", onBackIconClicked)
            TagsRow()
            DateRow()
          }
        }
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FindEventSearchBar(searchText: String, onBackIconClicked: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 8.dp)
              .background(
                  color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(56.dp))
              .padding(4.dp),
      verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onBackIconClicked() }) {
          Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go Back")
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = searchText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f))
      }
}

@Composable
fun TagsRow() {
  val tags = listOf("BBQ", "Student", "Vegan", "Beach")

  Row(
      modifier =
          Modifier.horizontalScroll(rememberScrollState())
              .padding(start = 8.dp, end = 0.dp, bottom = 8.dp, top = 8.dp)) {
        for (tag in tags) {
          Tag(text = tag, shape = RoundedCornerShape(50), modifier = Modifier.padding(end = 8.dp))
        }
      }
}

@Composable
fun Tag(text: String, shape: Shape, modifier: Modifier = Modifier) {
  Surface(shape = shape, modifier = modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier =
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .wrapContentWidth(Alignment.CenterHorizontally))
  }
}

@Composable
fun DateRow() {
  val date by remember { mutableStateOf("5 may 2024") }

  Row(
      modifier =
          Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
              .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(25))
              .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Icon(Icons.Rounded.CalendarToday, contentDescription = "Select Date")
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterVertically))
      }
}
