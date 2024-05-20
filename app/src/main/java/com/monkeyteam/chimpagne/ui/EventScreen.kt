package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.RemoveCircleOutline
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
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
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
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
                  modifier = Modifier.fillMaxWidth())
            },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(
                  onClick = {
                    if (accountViewModel.isUserLoggedIn()) {
                      coroutineScope.launch {
                        if (pagerState.currentPage == FindEventScreens.DETAIL) {
                          pagerState.scrollToPage(FindEventScreens.MAP)
                        } else {
                          navObject.goBack()
                        }
                      }
                    } else {
                      showPromptLogin = true
                    }
                  }) {
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
        if (uiState.currentUserRole == ChimpagneRole.NOT_IN_EVENT) {
          Button(
              onClick = { onJoinClick(eventViewModel.buildChimpagneEvent()) },
              modifier =
                  Modifier.fillMaxWidth().padding(8.dp).height(56.dp).testTag("button_search"),
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
        if (uiState.loading) {
          SpinnerView()
        } else {
          LazyColumn(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(innerPadding)
                      .background(MaterialTheme.colorScheme.background),
              verticalArrangement = Arrangement.Top) {
                item { ImageCard(uiState.imageUrl) }
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
                item { EventDescription(uiState.description, true) }
                item { ChimpagneDivider() }
                item { OrganiserView(uiState.ownerId, accountViewModel) }
                if (uiState.currentUserRole == ChimpagneRole.NOT_IN_EVENT) {
                  // Do nothing extra
                } else {
                  item {
                    SocialButtonRow(context = context, socialMediaLinks = uiState.socialMediaLinks)
                  }
                  item { ChimpagneDivider() }
                  item {
                    val iconList = mutableListOf<IconInfo>()
                    if (uiState.currentUserRole == ChimpagneRole.OWNER) {
                      iconList.add(
                          IconInfo(
                              icon = Icons.Rounded.Edit,
                              description =
                                  stringResource(id = R.string.event_details_screen_edit_button),
                              onClick = {
                                navObject.navigateTo(Route.EDIT_EVENT_SCREEN + "/${uiState.id}")
                              },
                              testTag = "edit"))
                      iconList.add(
                          IconInfo(
                              icon = Icons.Rounded.PeopleAlt,
                              description =
                                  stringResource(
                                      id = R.string.event_details_screen_manage_staff_button),
                              onClick = {
                                navObject.navigateTo(Route.MANAGE_STAFF_SCREEN + "/${uiState.id}")
                              },
                              testTag = "manage staff"))
                    } else {
                      iconList.add(
                          IconInfo(
                              icon = Icons.Rounded.RemoveCircleOutline,
                              description =
                                  stringResource(id = R.string.event_details_screen_leave_button),
                              onClick = {
                                if (accountViewModel.isUserLoggedIn()) {
                                  eventViewModel.leaveTheEvent(
                                      onSuccess = {
                                        showToast(
                                            context.getString(
                                                R.string.event_details_screen_leave_toast_success))
                                        navObject.goBack()
                                      })
                                } else {
                                  showPromptLogin = true
                                }
                              },
                              testTag = "leave"))
                    }
                    iconList.addAll(
                        listOf(
                            IconInfo(
                                icon = Icons.Rounded.ChatBubbleOutline,
                                description =
                                    stringResource(id = R.string.event_details_screen_chat_button),
                                onClick = {
                                  showToast("This function will be implemented in a future version")
                                },
                                testTag = "chat"),
                            IconInfo(
                                icon = Icons.Rounded.Backpack,
                                description =
                                    stringResource(
                                        id = R.string.event_details_screen_supplies_button),
                                onClick = {
                                  navObject.navigateTo(Route.SUPPLIES_SCREEN + "/" + uiState.id)
                                },
                                testTag = "supplies"),
                            IconInfo(
                                icon = Icons.Rounded.DirectionsCar,
                                description =
                                    stringResource(
                                        id = R.string.event_details_screen_car_pooling_button),
                                onClick = {
                                  showToast("This function will be implemented in a future version")
                                },
                                testTag = "car pooling"),
                            IconInfo(
                                icon = Icons.Rounded.Poll,
                                description =
                                    stringResource(
                                        id = R.string.event_details_screen_voting_button),
                                onClick = {
                                  showToast("This function will be implemented in a future version")
                                },
                                testTag = "polls"),
                            IconInfo(
                                icon = Icons.Rounded.Home,
                                description =
                                    stringResource(
                                        id = R.string.event_details_screen_bed_reservation),
                                onClick = {
                                  showToast("This function will be implemented in a future version")
                                },
                                testTag = "bed_reservation"),
                            IconInfo(
                                icon = Icons.Rounded.DirectionsCar,
                                description =
                                    stringResource(id = R.string.event_details_screen_parking),
                                onClick = {
                                  showToast("This function will be implemented in a future version")
                                },
                                testTag = "parking")))
                    IconRow(icons = iconList)
                  }
                }
              }
        }
      }
}

@Composable
fun IconRow(icons: List<IconInfo>) {
  Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(16.dp)) {
    icons.forEach { iconInfo ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier =
              Modifier.padding(horizontal = 16.dp)
                  .clickable(onClick = iconInfo.onClick)
                  .testTag(iconInfo.testTag)) {
            Icon(
                imageVector = iconInfo.icon,
                contentDescription = iconInfo.description,
                modifier = Modifier.size(40.dp))
            Text(
                text = iconInfo.description,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp))
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
