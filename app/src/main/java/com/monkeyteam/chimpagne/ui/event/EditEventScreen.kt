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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditEventScreen(
    initialPage: Int = 0,
    navObject: NavigationActions,
    eventViewModel: EventViewModel
) {
  // This screen is made of several panels
  // The user can go from panel either by swiping left and right,
  // or by clicking the buttons on the bottom of the screen.
  val uiState by eventViewModel.uiState.collectAsState()
  val pagerState = rememberPagerState(initialPage = initialPage) { 4 }
  val context = LocalContext.current
  Scaffold(
      topBar = {
        PanelTopBar(
            navObject = navObject,
            title = stringResource(id = R.string.edit_event),
            modifier = Modifier.testTag("edit_event_title"))
      },
      bottomBar = {
        PanelBottomBar(
            pagerState = pagerState,
            lastButtonText = stringResource(id = R.string.save_changes),
            lastButtonOnClick = {
              if (!uiState.loading) {
                eventViewModel.updateTheEvent(
                    onSuccess = {
                      Toast.makeText(
                              context,
                              context.getString(R.string.edit_event_toast_finish),
                              Toast.LENGTH_SHORT)
                          .show()
                      navObject.goBack()
                    },
                    onFailure = {
                      Toast.makeText(
                              context,
                              context.getString(R.string.edit_event_save_failure),
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
            2 -> AdvancedLogisticsPanel(eventViewModel)
            3 -> ChooseSocialsPanel(eventViewModel)
          }
        }
      }
}
