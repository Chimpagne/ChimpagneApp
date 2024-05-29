package com.monkeyteam.chimpagne.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.components.EventTagChip
import com.monkeyteam.chimpagne.ui.components.eventview.ChimpagneDivider
import com.monkeyteam.chimpagne.ui.components.eventview.EventActions
import com.monkeyteam.chimpagne.ui.components.eventview.EventDescription
import com.monkeyteam.chimpagne.ui.components.eventview.EventMainInfo
import com.monkeyteam.chimpagne.ui.components.eventview.ImageCard
import com.monkeyteam.chimpagne.ui.components.eventview.OrganiserView
import com.monkeyteam.chimpagne.ui.components.eventview.SimpleMapCard
import com.monkeyteam.chimpagne.ui.components.popUpCalendar
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.ui.utilities.WeatherPager
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EventScreen(
    navObject: NavigationActions,
    eventViewModel: EventViewModel,
    accountViewModel: AccountViewModel,
    pagerState: PagerState = rememberPagerState(initialPage = FindEventScreens.FORM) { 3 }
) {
  val uiState by eventViewModel.uiState.collectAsState()
  val accountUIState by accountViewModel.uiState.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  var showDialog by remember { mutableStateOf(false) }
  var showPromptLogin by remember { mutableStateOf(false) }
  var showQRDialog by remember { mutableStateOf(false) }
  var toast: Toast? by remember { mutableStateOf(null) }

  val showToast: (String) -> Unit = { message ->
    toast?.cancel()
    toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply { show() }
  }

  // Needed, otherwise screen doesnt update instantly after event is edited
  LaunchedEffect(Unit) { eventViewModel.fetchEvent() }

  val onJoinClick: (ChimpagneEvent) -> Unit = { event ->
    when {
      !accountViewModel.isUserLoggedIn() -> {
        showPromptLogin = true
      }
      event.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.NOT_IN_EVENT -> {
        showToast(context.getString(R.string.joining_toast) + event.title)
        eventViewModel.joinEvent(
            event.id,
            {
              showToast(context.getString(R.string.join_event_success))
              showDialog = true
            },
            { showToast(context.getString(R.string.join_event_failiure)) })
      }
      event.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.STAFF -> {
        showToast(context.getString(R.string.join_event_staff))
      }
      event.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.OWNER -> {
        showToast(context.getString(R.string.join_event_owner))
      }
      event.getRole(accountUIState.currentUserAccount?.firebaseAuthUID ?: "") ==
          ChimpagneRole.GUEST -> {
        showToast(context.getString(R.string.join_event_guest))
      }
    }
  }

  val goBack: () -> Unit = {
    coroutineScope.launch {
      if (pagerState.currentPage == FindEventScreens.DETAIL) {
        pagerState.scrollToPage(FindEventScreens.MAP)
      } else {
        navObject.goBack()
      }
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = uiState.title,
                  style = ChimpagneTypography.titleLarge,
                  textAlign = TextAlign.Center,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.fillMaxWidth().testTag("event title"))
            },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(onClick = { goBack() }, modifier = Modifier.testTag("go_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            },
            actions = {
              Box(
                  modifier =
                      Modifier.padding(end = 16.dp)
                          .shadow(6.dp, RoundedCornerShape(12.dp))
                          .clip(RoundedCornerShape(12.dp))
                          .background(MaterialTheme.colorScheme.primaryContainer)
                          .clickable { showQRDialog = true }
                          .padding(8.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.QrCodeScanner,
                        contentDescription = "Scan QR",
                        modifier = Modifier.size(36.dp).testTag("scan QR"),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                  }
            })
      },
      bottomBar = {
        if (uiState.id.isNotEmpty() && uiState.currentUserRole == ChimpagneRole.NOT_IN_EVENT) {
          Button(
              onClick = { onJoinClick(eventViewModel.buildChimpagneEvent()) },
              modifier = Modifier.fillMaxWidth().padding(8.dp).height(56.dp).testTag("join_button"),
              shape = MaterialTheme.shapes.extraLarge) {
                Icon(Icons.AutoMirrored.Rounded.Login, contentDescription = "join event")
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(id = R.string.find_event_join_event_button_text).uppercase(),
                    style = ChimpagneTypography.bodyLarge)
              }
        }
      }) { innerPadding ->
        if (showQRDialog) {
          QRCodeDialog(eventId = uiState.id, onDismiss = { showQRDialog = false })
        }
        if (showPromptLogin) {
          PromptLogin(context, navObject)
          showPromptLogin = false
        }
        if (showDialog) {
          popUpCalendar(
              onAccept = {
                createCalendarIntent(eventViewModel.buildChimpagneEvent())?.let {
                  context.startActivity(it)
                } ?: showToast(context.getString(R.string.calendar_failed))
                showDialog = false
              },
              onReject = { showDialog = false },
              event = eventViewModel.buildChimpagneEvent())
        }
        if (uiState.id.isEmpty()) {
          SpinnerView()
        } else {
          LazyColumn(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(innerPadding)
                      .background(MaterialTheme.colorScheme.background),
              verticalArrangement = Arrangement.Top) {
                item {
                  Log.d("EventScreen", "ImageUri: ${uiState}")
                  ImageCard(uiState.imageUri)
                }
                item {
                  Row(
                      modifier =
                          Modifier.horizontalScroll(rememberScrollState()).testTag("tag list")) {
                        uiState.tags.forEach { tag -> EventTagChip(tag) }
                      }
                }
                item { ChimpagneDivider() }
                item { EventMainInfo(event = eventViewModel.buildChimpagneEvent()) }
                item { ChimpagneDivider() }
                item {
                  EventDescription(
                      uiState.description, uiState.currentUserRole != ChimpagneRole.NOT_IN_EVENT)
                }
                item { SimpleMapCard(startingPosition = uiState.location) }
                item { ChimpagneDivider() }
                item { ChimpagneDivider() }
                item { WeatherPager(event = eventViewModel.buildChimpagneEvent()) }
                item { ChimpagneDivider() }
                item {
                  OrganiserView(
                      uiState.ownerId,
                      accountViewModel,
                      event = eventViewModel.buildChimpagneEvent())
                }
                if (uiState.currentUserRole == ChimpagneRole.NOT_IN_EVENT) {
                  // Do nothing extra
                } else {
                  item {
                    EventActions(
                        navObject, eventViewModel, accountViewModel.isUserLoggedIn(), showToast) {
                          showPromptLogin = it
                        }
                  }
                }
              }
        }
      }
}

@Composable
fun IconRow(icons: List<IconInfo>) {
  Row(
      modifier =
          Modifier.horizontalScroll(rememberScrollState())
              .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        icons.forEach { iconInfo ->
          Box(
              modifier =
                  Modifier.shadow(6.dp, RoundedCornerShape(12.dp))
                      .clip(RoundedCornerShape(12.dp))
                      .background(MaterialTheme.colorScheme.primaryContainer)
                      .clickable(onClick = iconInfo.onClick)
                      .padding(4.dp)
                      .testTag(iconInfo.testTag)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)) {
                      Icon(
                          imageVector = iconInfo.icon,
                          contentDescription = iconInfo.description,
                          modifier = Modifier.size(40.dp),
                          tint = MaterialTheme.colorScheme.onPrimaryContainer)
                      Text(
                          text = iconInfo.description,
                          fontSize = 12.sp,
                          textAlign = TextAlign.Center,
                          color = MaterialTheme.colorScheme.onPrimaryContainer,
                          modifier = Modifier.padding(top = 4.dp))
                    }
              }
        }
      }
}

data class IconInfo(
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit,
    val testTag: String
)
