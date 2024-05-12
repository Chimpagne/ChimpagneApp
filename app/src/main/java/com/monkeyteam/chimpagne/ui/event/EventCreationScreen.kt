package com.monkeyteam.chimpagne.ui.event

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

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

  val pagerState = rememberPagerState(initialPage = initialPage) { 4 }
  val context = LocalContext.current
  val uiState by eventViewModel.uiState.collectAsState()
  Scaffold(
      topBar = {
        PanelTopBar(
            navObject = navObject,
            title = stringResource(id = R.string.event_creation_screen_name),
            modifier = Modifier.testTag("create_event_title"))
      },
      bottomBar = {
        PanelBottomBar(
            pagerState = pagerState,
            lastButtonText = stringResource(id = R.string.event_creation_screen_create_event),
            lastButtonOnClick = {
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
            })
      }) { innerPadding ->
        HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
          when (page) {
            0 -> FirstPanel(eventViewModel)
            1 -> TagsAndPubPanel(eventViewModel)
            2 -> SuppliesPanel(eventViewModel)
            3 -> AdvancedLogisticsPanel(eventViewModel)
          }
        }
      }
}
