package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.RemoveCircleOutline
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.components.EventTagChip
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
import com.monkeyteam.chimpagne.ui.components.eventview.ChimpagneDivider
import com.monkeyteam.chimpagne.ui.components.eventview.EventDescription
import com.monkeyteam.chimpagne.ui.components.eventview.EventMainInfo
import com.monkeyteam.chimpagne.ui.components.eventview.ImageCard
import com.monkeyteam.chimpagne.ui.components.eventview.OrganiserView
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog
import com.monkeyteam.chimpagne.ui.utilities.SpinnerView
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDetailEventScreen(
    navObject: NavigationActions,
    eventViewModel: EventViewModel,
    accountViewModel: AccountViewModel
) {
  val uiState by eventViewModel.uiState.collectAsState()
  val context = LocalContext.current

  var showDialog by remember { mutableStateOf(false) }
  var showPromptLogin by remember { mutableStateOf(false) }

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
                      navObject.goBack()
                    } else {
                      showPromptLogin = true
                    }
                  },
                  modifier = Modifier.testTag("go back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                  }
            },
            actions = {
              IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = "Scan QR",
                    modifier = Modifier.size(36.dp).testTag("scan QR"))
              }
            })
      }) { innerPadding ->
        if (showDialog) {
          QRCodeDialog(eventId = uiState.id, onDismiss = { showDialog = false })
        }
        if (showPromptLogin) {
          PromptLogin(context, navObject)
          showPromptLogin = false
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
                item { OrganiserView(uiState.owner, accountViewModel) }
                item {
                  // MAP WILL BE ADDED HERE
                }
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
                                      Toast.makeText(
                                              context,
                                              context.getString(
                                                  R.string
                                                      .event_details_screen_leave_toast_success),
                                              Toast.LENGTH_SHORT)
                                          .show()
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
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
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
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              },
                              testTag = "car pooling"),
                          IconInfo(
                              icon = Icons.Rounded.Poll,
                              description =
                                  stringResource(id = R.string.event_details_screen_voting_button),
                              onClick = {
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              },
                              testTag = "polls"),
                          IconInfo(
                              icon = Icons.Rounded.Home,
                              description =
                                  stringResource(
                                      id = R.string.event_details_screen_bed_reservation),
                              onClick = {
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              },
                              testTag = "bed_reservation"),
                          IconInfo(
                              icon = Icons.Rounded.DirectionsCar,
                              description =
                                  stringResource(id = R.string.event_details_screen_parking),
                              onClick = {
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              },
                              testTag = "parking")))
                  IconRow(icons = iconList)
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
