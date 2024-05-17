package com.monkeyteam.chimpagne.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography

@Composable
fun DetailScreenSheet(
    event: ChimpagneEvent?,
    onJoinClick: (ChimpagneEvent) -> Unit = {},
    context: Context? = null
) {
  var showDialog by remember { mutableStateOf(false) }
  val enhancedOnJoinClick: (ChimpagneEvent) -> Unit = {
    onJoinClick(it)
    showDialog = true
  }
  if (event != null && event.id.isNotBlank()) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = event.title,
              style = ChimpagneTypography.headlineMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.startsAt().time.toString(),
              style = ChimpagneTypography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.endsAt().time.toString(),
              style = ChimpagneTypography.bodyMedium,
              modifier = Modifier.padding(bottom = 8.dp))

          Text(
              text = event.description,
              style = ChimpagneTypography.bodySmall,
              modifier = Modifier.padding(bottom = 8.dp))

          Row(
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                event.tags.forEach { tag -> SimpleTagChip(tag) }
              }

          Button(
              onClick = { enhancedOnJoinClick(event) },
              modifier = Modifier.align(Alignment.CenterHorizontally).testTag("join_button")) {
                Text(stringResource(id = R.string.find_event_join_event_button_text))
              }
        }
    if (showDialog && context != null) {
      popUpCalendar(
          onAccept = {
            createCalendarIntent(event)?.let { context.startActivity(it) }
                ?: Toast.makeText(context, R.string.calendar_failed, Toast.LENGTH_SHORT).show()
            showDialog = false
          },
          onReject = { showDialog = false },
          event = event)
    }
  } else {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
      Text(
          stringResource(id = R.string.find_event_no_event_available),
          style = ChimpagneTypography.bodyMedium)
    }
  }
}

@Composable
fun DetailScreenListSheet(
    events: List<ChimpagneEvent>,
    onJoinClick: (ChimpagneEvent) -> Unit = {},
    context: Context? = null
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxSheetHeight = screenHeight * 0.65f

    // State to keep track of the LazyColumn scroll position
    val lazyListState = rememberLazyListState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val isAtTop = lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
                val isAtBottom = lazyListState.layoutInfo.visibleItemsInfo.isNotEmpty() &&
                        lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lazyListState.layoutInfo.totalItemsCount - 1 &&
                        (lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.offset?.let { it + lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.size!! }
                            ?: 0) <= lazyListState.layoutInfo.viewportEndOffset

                return if ((isAtTop && available.y > 0) || (isAtBottom && available.y < 0)) {
                    available
                } else {
                    Velocity.Zero
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .heightIn(max = maxSheetHeight)
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection), // Add nested scroll modifier
        state = lazyListState // Attach LazyListState to track scroll position
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = { onJoinClick(event) }, // Use the provided onJoinClick callback
            )
        }
    }
}