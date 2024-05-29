package com.monkeyteam.chimpagne.ui.event

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.TopBar
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
  val pagerState = rememberPagerState(initialPage = initialPage) { 5 }
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val uiState by eventViewModel.uiState.collectAsState()

  fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  BackHandler {
    if (pagerState.currentPage > 0) {
      coroutineScope.launch { pagerState.scrollToPage(pagerState.currentPage - 1) }
    } else {
      navObject.goBack()
    }
  }

  Scaffold(
      topBar = {
        TopBar(
            text = stringResource(id = R.string.event_creation_screen_title),
            navigationIcon = { GoBackButton { navObject.goBack() } })
      },
      bottomBar = {
        PanelBottomBar(
            pagerState = pagerState,
            lastButtonText = stringResource(id = R.string.event_creation_screen_create_event),
            lastButtonOnClick = {
              Log.d("EventCreationScreen", "Create event button clicked ${uiState.loading}")
              if (!uiState.loading) {
                eventViewModel.createEvent(
                    onInvalidInputs = {
                      showToast(EventViewModel.eventInputValidityToString(it, context))
                    },
                    onSuccess = {
                      showToast(context.getString(R.string.event_creation_screen_toast_finish))
                      navObject.goBack()
                    })
              }
            })
      }) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding),
            beyondBoundsPageCount = 4) { page ->
              when (page) {
                0 -> FirstPanel(eventViewModel)
                1 -> TagsAndPubPanel(eventViewModel)
                2 -> SuppliesPanel(eventViewModel)
                3 -> AdditionalFeaturesPanel(eventViewModel)
                4 -> ChooseSocialsPanel(eventViewModel)
              }
            }
      }
}
