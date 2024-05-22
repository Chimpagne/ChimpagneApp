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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.CalendarButton
import com.monkeyteam.chimpagne.ui.components.ImageWithBlackFilterOverlay
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.ReportProblemButton
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.utilities.PromptLogin
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog
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
  val accountsState by accountViewModel.uiState.collectAsState()
  val context = LocalContext.current

  var showDialog by remember { mutableStateOf(false) }
  var showPromptLogin by remember { mutableStateOf(false) }
  val clipboardManager = LocalClipboardManager.current
  // Otherwise event doesn't directly load
  LaunchedEffect(Unit) { eventViewModel.fetchEvent {} }
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Row(
                  modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                  verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = uiState.title,
                        fontSize = 30.sp,
                        fontFamily = ChimpagneFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("event title"))
                    Spacer(modifier = Modifier.weight(1f))
                  }
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
              Box(
                  modifier =
                      Modifier.padding(end = 16.dp)
                          .shadow(10.dp, RoundedCornerShape(12.dp))
                          .clip(RoundedCornerShape(12.dp))
                          .background(MaterialTheme.colorScheme.primaryContainer)
                          .clickable { showDialog = true }
                          .padding(8.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.QrCodeScanner,
                        contentDescription = "Scan QR",
                        modifier = Modifier.size(36.dp).testTag("scan QR"),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
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
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)) {
              LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                item {
                  Box(
                      modifier =
                          Modifier.height(200.dp)
                              .fillMaxWidth()
                              .padding(horizontal = 16.dp, vertical = 8.dp)
                              .clip(RoundedCornerShape(12.dp))
                              .background(MaterialTheme.colorScheme.primaryContainer),
                      contentAlignment = Alignment.Center) {
                        ImageWithBlackFilterOverlay(uiState.image)
                      }
                }
                item {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier =
                                Modifier.horizontalScroll(rememberScrollState())
                                    .testTag("tag list")) {
                              uiState.tags.forEach { tag ->
                                Box(
                                    modifier =
                                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 16.dp, vertical = 8.dp)) {
                                      SimpleTagChip(tag)
                                    }
                              }
                            }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray)
                        Column(
                            modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                              Row(
                                  modifier = Modifier.fillMaxWidth(),
                                  horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                      Text(
                                          text = stringResource(id = R.string.date_tools_from),
                                          fontFamily = ChimpagneFontFamily,
                                          fontSize = 16.sp,
                                          color = Color.Gray)
                                      Column(
                                          horizontalAlignment = Alignment.CenterHorizontally,
                                          modifier = Modifier.testTag("event date")) {
                                            Text(
                                                text =
                                                    simpleDateFormat(
                                                        buildTimestamp(
                                                            uiState.startsAtCalendarDate)),
                                                fontFamily = ChimpagneFontFamily,
                                                fontSize = 16.sp,
                                                color =
                                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.Bold)
                                            Text(
                                                text =
                                                    simpleTimeFormat(
                                                        buildTimestamp(
                                                            uiState.startsAtCalendarDate)),
                                                fontFamily = ChimpagneFontFamily,
                                                fontSize = 16.sp,
                                                color =
                                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.Bold)
                                          }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                          CalendarButton(
                                              event = eventViewModel.buildChimpagneEvent(),
                                              contextMainActivity = context)
                                          Box(
                                              modifier =
                                                  Modifier.shadow(10.dp, RoundedCornerShape(12.dp))
                                                      .clip(RoundedCornerShape(12.dp))
                                                      .background(
                                                          MaterialTheme.colorScheme
                                                              .primaryContainer)
                                                      .clickable {
                                                        val annotatedString = buildAnnotatedString {
                                                          append(
                                                              getString(
                                                                  context,
                                                                  R.string.deep_link_url_event) +
                                                                  uiState.id)
                                                        }
                                                        clipboardManager.setText(annotatedString)
                                                      }
                                                      .padding(8.dp)
                                                      .testTag("share")) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Share,
                                                    contentDescription = "Share Event",
                                                    tint =
                                                        MaterialTheme.colorScheme
                                                            .onPrimaryContainer,
                                                    modifier =
                                                        Modifier.size(36.dp).testTag("share"))
                                              }
                                        }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(start = 8.dp)) {
                                          Text(
                                              text = stringResource(id = R.string.date_tools_until),
                                              fontFamily = ChimpagneFontFamily,
                                              fontSize = 16.sp,
                                              color = Color.Gray)
                                          Column(
                                              horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text =
                                                        simpleDateFormat(
                                                            buildTimestamp(
                                                                uiState.endsAtCalendarDate)),
                                                    fontFamily = ChimpagneFontFamily,
                                                    fontSize = 16.sp,
                                                    color =
                                                        MaterialTheme.colorScheme
                                                            .onPrimaryContainer,
                                                    fontWeight = FontWeight.Bold)
                                                Text(
                                                    text =
                                                        simpleTimeFormat(
                                                            buildTimestamp(
                                                                uiState.endsAtCalendarDate)),
                                                    fontFamily = ChimpagneFontFamily,
                                                    fontSize = 16.sp,
                                                    color =
                                                        MaterialTheme.colorScheme
                                                            .onPrimaryContainer,
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
                                            Modifier.clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer)
                                                .padding(horizontal = 24.dp, vertical = 12.dp)
                                                .testTag("number_of_guests")) {
                                          Text(
                                              text =
                                                  "${uiState.guests.count()} ${stringResource(id = R.string.event_details_screen_number_of_guests)}",
                                              fontFamily = ChimpagneFontFamily,
                                              fontWeight = FontWeight.Bold,
                                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                                              modifier = Modifier)
                                        }
                                  }
                            }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray)

                        var expandedDescription by remember { mutableStateOf(false) }
                        val maxLines = if (expandedDescription) Int.MAX_VALUE else 3
                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .clickable { expandedDescription = !expandedDescription }
                                    .padding(horizontal = 16.dp)
                                    .testTag("description")) {
                              Text(
                                  text = uiState.description,
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

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                              ProfileIcon(
                                  uri = accountsState.currentUserProfilePicture,
                                  onClick = {
                                    Toast.makeText(
                                            context,
                                            "This function will be implemented in a future version",
                                            Toast.LENGTH_SHORT)
                                        .show()
                                  })
                              Text(
                                  text =
                                      "${
                                    stringResource(
                                        id = R.string.event_details_screen_organized_by
                                    )
                                }\n ${accountsState.currentUserAccount?.firstName} ${accountsState.currentUserAccount?.lastName}",
                                  fontSize = 14.sp,
                                  fontFamily = ChimpagneFontFamily,
                                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                                  modifier = Modifier.padding(start = 20.dp, end = 20.dp))

                              Spacer(modifier = Modifier.height(8.dp))

                              ReportProblemButton {
                                Toast.makeText(
                                        context,
                                        "This function will be implemented in a future version",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              }
                            }

                        Spacer(Modifier.height(16.dp))

                        // MAP WILL BE ADDED HERE

                        SocialButtonRow(
                            context = context, socialMediaLinks = uiState.socialMediaLinks)

                        Spacer(Modifier.height(16.dp))

                        val iconList = mutableListOf<IconInfo>()

                        if (uiState.currentUserRole == ChimpagneRole.OWNER) {
                          iconList.add(
                              IconInfo(
                                  icon = Icons.Rounded.Edit,
                                  description =
                                      stringResource(
                                          id = R.string.event_details_screen_edit_button),
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
                                    navObject.navigateTo(
                                        Route.MANAGE_STAFF_SCREEN + "/${uiState.id}")
                                  },
                                  testTag = "manage staff"))
                        } else {
                          iconList.add(
                              IconInfo(
                                  icon = Icons.Rounded.RemoveCircleOutline,
                                  description =
                                      stringResource(
                                          id = R.string.event_details_screen_leave_button),
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
                                    icon = Icons.Rounded.Backpack,
                                    description =
                                        stringResource(
                                            id = R.string.event_details_screen_supplies_button),
                                    onClick = {
                                      navObject.navigateTo(Route.SUPPLIES_SCREEN + "/" + uiState.id)
                                    },
                                    testTag = "supplies"),
                                IconInfo(
                                    icon = Icons.Rounded.Poll,
                                    description =
                                        stringResource(
                                            id = R.string.event_details_screen_voting_button),
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
                                            id = R.string.event_details_screen_accommodation),
                                    onClick = {
                                      Toast.makeText(
                                              context,
                                              "This function will be implemented in a future version",
                                              Toast.LENGTH_SHORT)
                                          .show()
                                    },
                                    testTag = "accommodation")))

                        IconRow(icons = iconList)
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
                  Modifier.shadow(10.dp, RoundedCornerShape(12.dp))
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
