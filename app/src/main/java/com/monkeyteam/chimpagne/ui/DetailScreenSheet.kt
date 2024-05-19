package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.ImageWithBlackFilterOverlay
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenSheet(
    goBack: () -> Unit = {},
    event: ChimpagneEvent?,
    onJoinClick: (ChimpagneEvent) -> Unit = {}
) {

  val clipboardManager = LocalClipboardManager.current

  val context = LocalContext.current

  var showQRDialog by remember { mutableStateOf(false) }
  var showDialog by remember { mutableStateOf(false) }
  val enhancedOnJoinClick: (ChimpagneEvent) -> Unit = {
    onJoinClick(it)
    showDialog = true
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = event?.title ?: "",
                  style = ChimpagneTypography.titleLarge,
                  textAlign = TextAlign.Center,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.fillMaxWidth())
            },
            modifier = Modifier.shadow(4.dp).testTag("find_event_title"),
            navigationIcon = {
              IconButton(onClick = { goBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            },
            actions = {
              IconButton(onClick = { showQRDialog = true }) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scan QR",
                    modifier = Modifier.size(36.dp).testTag("scan QR"))
              }
            })
      },
      bottomBar = {
        Button(
            onClick = {
              if (event != null) {
                enhancedOnJoinClick(event)
              }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp).height(56.dp).testTag("button_search"),
            shape = MaterialTheme.shapes.extraLarge) {
              Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = "join event")
              Spacer(Modifier.width(8.dp))
              Text(
                  stringResource(id = R.string.find_event_join_event_button_text).uppercase(),
                  style = ChimpagneTypography.bodyLarge)
            }
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).testTag("detail_screen").fillMaxSize()) {
          if (event != null && event.id.isNotBlank()) {
            if (showQRDialog) {
              QRCodeDialog(eventId = event.id, onDismiss = { showQRDialog = false })
            }
            if (showDialog) {
              popUpCalendar(
                  onAccept = {
                    createCalendarIntent(event)?.let { context.startActivity(it) }
                        ?: Toast.makeText(context, R.string.calendar_failed, Toast.LENGTH_SHORT)
                            .show()
                    showDialog = false
                  },
                  onReject = { showDialog = false },
                  event = event)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  item {
                    Box(
                        modifier =
                            Modifier.height(200.dp)
                                .fillMaxWidth()
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center) {
                          ImageWithBlackFilterOverlay(event.imageUrl)
                        }
                  }
                  item {
                    Row(
                        modifier =
                            Modifier.horizontalScroll(rememberScrollState()).testTag("tag list")) {
                          event.tags.forEach { tag ->
                            Box(
                                modifier =
                                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        .shadow(
                                            elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)) {
                                  SimpleTagChip(tag)
                                }
                          }
                        }
                  }
                  item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray)
                  }
                  item {
                    Column(
                        modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                          Row(
                              modifier = Modifier.fillMaxWidth().testTag("event date"),
                              horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                  Text(
                                      text = stringResource(id = R.string.date_tools_from),
                                      fontFamily = ChimpagneFontFamily,
                                      fontSize = 16.sp,
                                      color = Color.Gray)
                                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = simpleDateFormat(event.startsAtTimestamp),
                                        fontFamily = ChimpagneFontFamily,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold)
                                    Text(
                                        text = simpleTimeFormat(event.startsAtTimestamp),
                                        fontFamily = ChimpagneFontFamily,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold)
                                  }
                                }
                                CalendarButton(event = event, contextMainActivity = context)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                  Text(
                                      text = stringResource(id = R.string.date_tools_until),
                                      fontFamily = ChimpagneFontFamily,
                                      fontSize = 16.sp,
                                      color = Color.Gray)
                                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = simpleDateFormat(event.endsAtTimestamp),
                                        fontFamily = ChimpagneFontFamily,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold)
                                    Text(
                                        text = simpleTimeFormat(event.endsAtTimestamp),
                                        fontFamily = ChimpagneFontFamily,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold)
                                  }
                                }
                              }
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.Center,
                              modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Box(
                                    modifier =
                                        Modifier.shadow(
                                                elevation = 10.dp,
                                                shape = RoundedCornerShape(16.dp))
                                            .clip(RoundedCornerShape(50))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 24.dp, vertical = 12.dp)) {
                                      Text(
                                          text =
                                              "${event.guests.count()} ${stringResource(id = R.string.event_details_screen_number_of_guests)}",
                                          fontFamily = ChimpagneFontFamily,
                                          fontWeight = FontWeight.Bold,
                                          color = MaterialTheme.colorScheme.onPrimaryContainer,
                                          modifier = Modifier.testTag("number of guests"))
                                    }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                      val annotatedString = buildAnnotatedString {
                                        append(
                                            ContextCompat.getString(
                                                context, R.string.deep_link_url_event) + event.id)
                                      }
                                      clipboardManager.setText(annotatedString)
                                    },
                                    modifier = Modifier.size(36.dp).testTag("share")) {
                                      Icon(
                                          imageVector = Icons.Rounded.Share,
                                          contentDescription = "Share Event",
                                          tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                              }
                        }
                  }
                  item {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray)
                  }
                  item {
                    var expandedDescription by remember { mutableStateOf(false) }
                    val maxLines = if (expandedDescription) Int.MAX_VALUE else 3
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .testTag("description")
                                .clickable { expandedDescription = !expandedDescription }
                                .padding(horizontal = 16.dp)) {
                          Text(
                              text = event.description,
                              fontSize = 16.sp,
                              fontFamily = ChimpagneFontFamily,
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              maxLines = maxLines,
                              modifier = Modifier.weight(1f))
                          Icon(
                              imageVector =
                                  if (expandedDescription) Icons.Filled.ArrowDropUp
                                  else Icons.Filled.ArrowDropDown,
                              contentDescription =
                                  if (expandedDescription) "Collapse" else "Expand",
                              tint = MaterialTheme.colorScheme.primary)
                        }
                  }
                  item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray)
                  }
                }
          } else {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center) {
                  Text(
                      stringResource(id = R.string.find_event_no_event_available),
                      style = ChimpagneTypography.bodyMedium)
                }
          }
        }
      }
}

@Composable
fun DetailScreenListSheet(
    events: List<ChimpagneEvent>,
    onEventClick: (ChimpagneEvent) -> Unit = {}
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
          Modifier.heightIn(max = maxSheetHeight)
              .fillMaxWidth()
              .nestedScroll(nestedScrollConnection), // Add nested scroll modifier
      state = lazyListState // Attach LazyListState to track scroll position
      ) {
        items(events) { event ->
          EventCard(
              event = event,
              onClick = { onEventClick(event) }, // Use the provided onJoinClick callback
          )
        }
      }
}
