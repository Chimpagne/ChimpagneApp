package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneEventId
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.components.EventCard
import com.monkeyteam.chimpagne.ui.components.EventTagChip
import com.monkeyteam.chimpagne.ui.components.eventview.ChimpagneDivider
import com.monkeyteam.chimpagne.ui.components.eventview.EventDescription
import com.monkeyteam.chimpagne.ui.components.eventview.EventMainInfo
import com.monkeyteam.chimpagne.ui.components.eventview.ImageCard
import com.monkeyteam.chimpagne.ui.components.eventview.OrganiserView
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.FindEventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenSheet(
    goBack: () -> Unit = {},
    event: ChimpagneEvent?,
    joinEvent: (ChimpagneEventId, () -> Unit, (Exception) -> Unit) -> Unit,
    accountViewModel: AccountViewModel,
    navObject: NavigationActions
) {

  val accountUIState by accountViewModel.uiState.collectAsState()

  var showPromptLogin by remember { mutableStateOf(false) }

  var showQRDialog by remember { mutableStateOf(false) }
  var showDialog by remember { mutableStateOf(false) }

  val context = LocalContext.current

  var toast: Toast? by remember { mutableStateOf(null) }

  val showToast: (String) -> Unit = { message ->
    toast?.cancel()
    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply { show() }
  }

  val stringResJoining = stringResource(id = R.string.joining_toast)
  val stringResFailure = stringResource(id = R.string.join_event_failiure)
  val stringResSuccess = stringResource(id = R.string.join_event_success)

  val stringResStaff = stringResource(id = R.string.join_event_staff)
  val stringResGuest = stringResource(id = R.string.join_event_guest)
  val stringResOwner = stringResource(id = R.string.join_event_owner)

  val onJoinClick: (ChimpagneEvent) -> Unit = {
    when {
      // Check if the user is not logged in
      !accountViewModel.isUserLoggedIn() -> {
        // Redirect user to login screen
        showPromptLogin = true
      }

      // The user has not yet joined the event
      it.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.NOT_IN_EVENT -> {
        it.let {
          showToast("$stringResJoining ${it.title}")

          joinEvent(
              it.id,
              {
                  showToast(stringResSuccess)
                showDialog = true
              },
              { showToast(stringResFailure)})

          navObject.clearAndNavigateTo(Route.VIEW_DETAIL_EVENT_SCREEN + "/${it.id})", false)
        }
      }

      // The user has already joined the event, or is a staff for the event,or is the organizer
      it.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.STAFF -> {
          showToast(stringResStaff)
      }
      it.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.OWNER -> {
          showToast(stringResOwner)
      }
      it.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.GUEST -> {
          showToast(stringResGuest)
      }
    }
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
                onJoinClick(event)
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
        if (showPromptLogin) {
          PromptLogin(context, navObject)
          showPromptLogin = false
        }
        Box(modifier = Modifier.padding(innerPadding).testTag("detail_screen").fillMaxSize()) {
          if (event != null && event.id.isNotBlank()) {
            if (showQRDialog) {
              QRCodeDialog(eventId = event.id, onDismiss = { showQRDialog = false })
            }
            if (showDialog) {
              popUpCalendar(
                  onAccept = {
                    createCalendarIntent(event)?.let { context.startActivity(it) }
                        ?: showToast(stringResource(R.string.calendar_failed))
                    showDialog = false
                  },
                  onReject = { showDialog = false },
                  event = event)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Top) {
                  item { ImageCard(event.imageUrl) }
                  item {
                    Row(
                        modifier =
                            Modifier.horizontalScroll(rememberScrollState()).testTag("tag list")) {
                          event.tags.forEach { tag -> EventTagChip(tag) }
                        }
                  }
                  item { ChimpagneDivider() }
                  item { EventMainInfo(event = event) }
                  item { ChimpagneDivider() }
                  item { EventDescription(event.description, true) }
                  item { ChimpagneDivider() }
                  item { OrganiserView(event.ownerId, accountViewModel) }
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
