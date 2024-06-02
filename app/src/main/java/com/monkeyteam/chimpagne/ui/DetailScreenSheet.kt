package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.ui.components.EventCard

/**
 * Composable function to display a list of events in a sheet that occupies up to 75% of the screen
 * height.
 *
 * @param events List of ChimpagneEvent objects to be displayed in the list.
 * @param onEventClick Lambda function to be executed when an event item is clicked. Defaults to an
 *   empty lambda.
 */
@Composable
fun DetailScreenListSheet(
    events: List<ChimpagneEvent>,
    onEventClick: (ChimpagneEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val maxSheetHeight = screenHeight * 0.75f

  // State to keep track of the LazyColumn scroll position
  val lazyListState = rememberLazyListState()
  val nestedScrollConnection = remember {
    object : NestedScrollConnection {

      override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val isAtTop =
            lazyListState.firstVisibleItemIndex == 0 &&
                lazyListState.firstVisibleItemScrollOffset == 0
        val isAtBottom =
            lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() &&
                lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ==
                    lazyListState.layoutInfo.totalItemsCount - 1 &&
                (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.offset?.let {
                  it + lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.size!!
                } ?: 0) <= lazyListState.layoutInfo.viewportEndOffset

        return if ((isAtTop && available.y > 0) || (isAtBottom && available.y < 0)) {
          available
        } else {
          Velocity.Zero
        }
      }
    }
  }

  LazyColumn(
      modifier =
          modifier
              .heightIn(max = maxSheetHeight)
              .fillMaxWidth()
              .nestedScroll(nestedScrollConnection)
              .testTag("bottom sheet"), // Add nested scroll modifier
      state = lazyListState // Attach LazyListState to track scroll position
      ) {
        items(events.sortedBy { it.startsAtTimestamp }) { event ->
          EventCard(
              event = event,
              modifier = Modifier.testTag(event.id),
              onClick = { onEventClick(event) }, // Use the provided onJoinClick callback
          )
        }
      }
}
