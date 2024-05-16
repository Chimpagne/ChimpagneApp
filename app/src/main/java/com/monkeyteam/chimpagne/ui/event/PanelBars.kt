package com.monkeyteam.chimpagne.ui.event

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelTopBar(navObject: NavigationActions, title: String, modifier: Modifier = Modifier) {
  TopAppBar(
      title = { Text(title) },
      modifier = modifier.shadow(4.dp),
      navigationIcon = {
        IconButton(onClick = { navObject.goBack() }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
        }
      })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PanelBottomBar(
    pagerState: PagerState,
    lastButtonText: String,
    lastButtonOnClick: () -> Unit,
) {
  // The logic below is to make sure to display the proper panel navigation button
  // throughout the panels
  val coroutineScope = rememberCoroutineScope()

  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    if (pagerState.currentPage > 0) {
      Button(
          onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
          },
          modifier = Modifier.testTag("previous_button")) {
            Text(stringResource(id = R.string.event_creation_screen_previous))
          }
    } else {
      Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))
    }
    if (pagerState.currentPage < pagerState.pageCount - 1) {
      Button(
          onClick = {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
          },
          modifier = Modifier.testTag("next_button")) {
            Text(stringResource(id = R.string.event_creation_screen_next))
          }
    } else {
      Button(onClick = lastButtonOnClick, modifier = Modifier.testTag("last_button")) {
        Text(lastButtonText)
      }
    }
  }
}
