package com.monkeyteam.chimpagne.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAnEventScreen(navObject: NavigationActions) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }
  /*Scaffold(
     floatingActionButton = {
       ExtendedFloatingActionButton(
           text = { Text("Show bottom sheet") },
           icon = { Icon(Icons.Filled.Add, contentDescription = "") },
           onClick = { showBottomSheet = true })
     }) { contentPadding ->
       // Screen content

       if (showBottomSheet) {
         ModalBottomSheet(
             onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
               // Sheet content
               Button(
                   onClick = {
                     scope
                         .launch { sheetState.hide() }
                         .invokeOnCompletion {
                           if (!sheetState.isVisible) {
                             showBottomSheet = false
                           }
                         }
                   }) {
                     Text("Hide bottom sheet")
                   }
             }
       }
     }

  */
}