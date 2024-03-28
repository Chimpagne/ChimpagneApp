package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.components.FindEventSearchBar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.md_theme_dark_background
import com.monkeyteam.chimpagne.ui.utilities.MapContainer
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun FindAnEventScreen(navObject: NavigationActions) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()

  // Apply padding to ensure content is displayed below the system bars
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
          MapContainer(
              Modifier.padding(
                  top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                  bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()))

          Column {
            FindEventSearchBar()
            TagsRow()
          }
        }
      }
}

@Composable
fun TagsRow() {
  // Sample list of tags, you can replace it with your actual data
  val tags = listOf("Date", "BBQ", "Student", "Vegan", "Beach", "More Tags")

  // Using Row with a horizontal scroll modifier
  Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
    // Date Picker Tag with angular corners
    Tag(
        text = tags.first(),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.padding(start = 8.dp))

    // Spacer for visual separation
    Spacer(modifier = Modifier.width(8.dp))

    // Tags with rounded corners
    tags.drop(1).forEach { tag ->
      Tag(text = tag, shape = RoundedCornerShape(50), modifier = Modifier.padding(end = 8.dp))
    }
  }
}

@Composable
fun Tag(text: String, shape: Shape, modifier: Modifier = Modifier) {
  Surface(color = md_theme_dark_background, shape = shape, modifier = modifier) {
    Text(
        text = text,
        modifier =
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .wrapContentWidth(Alignment.CenterHorizontally))
  }
}
