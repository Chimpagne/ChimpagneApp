package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.RemoveCircleOutline
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.simpleDateFormat
import com.monkeyteam.chimpagne.model.utils.simpleTimeFormat
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.utilities.QRCodeDialog
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDetailEventScreen(navObject: NavigationActions, eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()
  val context = LocalContext.current

  var showDialog by remember { mutableStateOf(false) }

  val userRole = eventViewModel.getCurrentUserRole()

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.CenterHorizontally) {
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
              IconButton(onClick = { navObject.goBack() }, modifier = Modifier.testTag("go back")) {
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
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)) {
              LazyColumn(
                  modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    item {
                      Box(
                          modifier =
                              Modifier.height(250.dp)
                                  .fillMaxWidth()
                                  .padding(horizontal = 16.dp, vertical = 16.dp)
                                  .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp))
                                  .clip(RoundedCornerShape(16.dp))
                                  .background(MaterialTheme.colorScheme.primaryContainer),
                          contentAlignment = Alignment.Center) {
                            // For now, image is static -> needs to be added in the UI State for
                            // event view model
                            Image(
                                painter = painterResource(id = R.drawable.default_party_image),
                                contentDescription = "Event Banner",
                                modifier =
                                    Modifier.matchParentSize()
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop)
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
                                                .shadow(
                                                    elevation = 10.dp,
                                                    shape = RoundedCornerShape(16.dp))
                                                .clip(RoundedCornerShape(50))
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer)
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
                                      horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                          Text(
                                              text = stringResource(id = R.string.date_tools_from),
                                              fontFamily = ChimpagneFontFamily,
                                              fontSize = 16.sp,
                                              color = Color.Gray)
                                          Column(
                                              horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text =
                                                        simpleDateFormat(
                                                            buildTimestamp(
                                                                uiState.startsAtCalendarDate)),
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
                                                                uiState.startsAtCalendarDate)),
                                                    fontFamily = ChimpagneFontFamily,
                                                    fontSize = 16.sp,
                                                    color =
                                                        MaterialTheme.colorScheme
                                                            .onPrimaryContainer,
                                                    fontWeight = FontWeight.Bold)
                                              }
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                  Box(
                                      modifier =
                                          Modifier.shadow(
                                                  elevation = 10.dp,
                                                  shape = RoundedCornerShape(16.dp))
                                              .clip(RoundedCornerShape(50))
                                              .background(
                                                  MaterialTheme.colorScheme.primaryContainer)
                                              .padding(horizontal = 24.dp, vertical = 8.dp)) {
                                        Text(
                                            text =
                                                "${uiState.guests.count()} ${stringResource(
                                  id = R.string.event_details_screen_number_of_guests)}",
                                            fontFamily = ChimpagneFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.testTag("number of guests"))
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
                                        .padding(horizontal = 16.dp)) {
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
                                  Image(
                                      // in a future version, we should dynamically show the owner's
                                      // profile pic
                                      painter =
                                          painterResource(
                                              id = R.drawable.default_user_profile_picture),
                                      contentDescription = "Organizer Avatar",
                                      modifier =
                                          Modifier.size(60.dp).clip(CircleShape).clickable {
                                            Toast.makeText(
                                                    context,
                                                    "This function will be implemented in a future version",
                                                    Toast.LENGTH_SHORT)
                                                .show()
                                          })
                                  Spacer(modifier = Modifier.width(12.dp))

                                  Text(
                                      text = " Organized by \n Alice123",
                                      fontSize = 14.sp,
                                      fontFamily = ChimpagneFontFamily,
                                      color = MaterialTheme.colorScheme.onPrimaryContainer)

                                  Spacer(modifier = Modifier.weight(1f))

                                  Button(
                                      onClick = {
                                        Toast.makeText(
                                                context,
                                                "This function will be implemented in a future version",
                                                Toast.LENGTH_SHORT)
                                            .show()
                                      },
                                      modifier = Modifier.testTag("reportProblem")) {
                                        Text(
                                            text =
                                                stringResource(
                                                    id =
                                                        R.string
                                                            .event_details_screen_report_problem),
                                            color = Color.White)
                                        Spacer(modifier = Modifier.size(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Report a Problem",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp))
                                      }
                                }

                            if (uiState.currentUserRole != ChimpagneRole.OWNER) {

                              ChimpagneButton(
                                  text =
                                      stringResource(
                                          id = R.string.event_details_screen_leave_button),
                                  icon = Icons.Rounded.RemoveCircleOutline,
                                  fontWeight = FontWeight.Bold,
                                  fontSize = 30.sp,
                                  modifier =
                                      Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                          .fillMaxWidth()
                                          .testTag("leave"),
                                  onClick = {
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
                                  })
                            }

                            // Only the owner can edit the event settings
                            if (uiState.currentUserRole == ChimpagneRole.OWNER) {

                              ChimpagneButton(
                                  text =
                                      stringResource(
                                          id = R.string.event_details_screen_edit_button),
                                  icon = Icons.Rounded.Edit,
                                  fontWeight = FontWeight.Bold,
                                  fontSize = 30.sp,
                                  modifier =
                                      Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                          .fillMaxWidth()
                                          .testTag("edit"),
                                  onClick = {
                                    navObject.navigateTo(Route.EDIT_EVENT_SCREEN + "/${uiState.id}")
                                  })
                            }
                            Spacer(Modifier.height(16.dp))
                            ChimpagneButton(
                                text =
                                    stringResource(id = R.string.event_details_screen_chat_button),
                                icon = Icons.Rounded.ChatBubbleOutline,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .testTag("chat"),
                                onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                          context,
                                          "This function will be implemented in a future version",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                })
                            Spacer(Modifier.height(16.dp))
                            ChimpagneButton(
                                text =
                                    stringResource(
                                        id = R.string.event_details_screen_location_button),
                                icon = Icons.Rounded.LocationOn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .testTag("location"),
                                onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                          context,
                                          "This function will be implemented in a future version",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                })
                            Spacer(Modifier.height(16.dp))
                            ChimpagneButton(
                                text =
                                    stringResource(
                                        id = R.string.event_details_screen_supplies_button),
                                icon = Icons.Rounded.Backpack,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .testTag("supplies"),
                                onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                          context,
                                          "This function will be implemented in a future version",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                })
                            Spacer(Modifier.height(16.dp))
                            ChimpagneButton(
                                text =
                                    stringResource(
                                        id = R.string.event_details_screen_voting_button),
                                icon = Icons.Rounded.Poll,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .testTag("polls"),
                                onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                          context,
                                          "This function will be implemented in a future version",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                })
                            Spacer(Modifier.height(16.dp))
                            ChimpagneButton(
                                text =
                                    stringResource(
                                        id = R.string.event_details_screen_car_pooling_button),
                                icon = Icons.Rounded.DirectionsCar,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier =
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        .fillMaxWidth()
                                        .testTag("car pooling"),
                                onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                          context,
                                          "This function will be implemented in a future version",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                })
                          }
                    }
                  }
            }
      }
}
