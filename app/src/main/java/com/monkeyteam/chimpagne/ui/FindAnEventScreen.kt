package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAnEventScreen(navObject: NavigationActions) {
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  BottomSheetScaffold(
    sheetContent = {
      Box(
        Modifier
          .fillMaxWidth()
          .height(128.dp),
        contentAlignment = Alignment.Center
      ) {
        Text("Swipe up to expand sheet")
      }
      Column(
        Modifier
          .fillMaxWidth()
          .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("Sheet content")
        Spacer(Modifier.height(20.dp))
        Button(
          onClick = {
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
          }
        ) {
          Text("Click to collapse sheet")
        }
      }
    },
    scaffoldState = scaffoldState,
    sheetPeekHeight = 128.dp
  ) { innerPadding ->
    LazyColumn(contentPadding = innerPadding) {
      items(100) {
        Box(
          Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Blue)
        )
      }
    }
  }
}
