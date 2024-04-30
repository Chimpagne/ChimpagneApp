package com.monkeyteam.chimpagne.ui.event

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventCreationScreen(
    initialPage: Int = 0,
    navObject: NavigationActions,
    eventViewModel: EventViewModel
) {
  // This screen is made of several panels
  // The user can go from panel either by swiping left and right,
  // or by clicking the buttons on the bottom of the screen.

  val uiState by eventViewModel.uiState.collectAsState()

  val pagerState = rememberPagerState(initialPage = initialPage) { 4 }
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  Column {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
      GoBackButton(navigationActions = navObject)
      Text(text = stringResource(id = R.string.event_creation_screen_name))
    }
    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
      when (page) {
        0 -> FirstPanel(eventViewModel)
        1 -> SecondPanel(eventViewModel)
        2 -> ThirdPanel(eventViewModel)
        3 -> FourthPanel(eventViewModel)
      }
    }

    // The logic below is to make sure to display the proper panel navigation button
    // throughout the panels
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
      if (pagerState.currentPage < 3) {
        Button(
            onClick = {
              coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            modifier = Modifier.testTag("next_button")) {
              Text(stringResource(id = R.string.event_creation_screen_next))
            }
      } else {
        Button(
            onClick = {
              if (!uiState.loading) {
                Toast.makeText(
                        context,
                        context.getString(R.string.event_creation_screen_toast_creating),
                        Toast.LENGTH_SHORT)
                    .show()
                eventViewModel.createTheEvent(
                    onSuccess = {
                      Toast.makeText(
                              context,
                              context.getString(R.string.event_creation_screen_toast_finish),
                              Toast.LENGTH_SHORT)
                          .show()
                      navObject.goBack()
                    })
              }
            },
            modifier = Modifier.testTag("create_event_button")) {
              Text(stringResource(id = R.string.event_creation_screen_create_event))
            }
      }
    }
  }
}
