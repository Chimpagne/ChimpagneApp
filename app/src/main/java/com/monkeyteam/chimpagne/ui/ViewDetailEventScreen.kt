package com.monkeyteam.chimpagne.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Poll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.timestampToStringWithDateAndTime
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDetailEventScreen(navObject: NavigationActions, eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()
  val context = LocalContext.current

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = uiState.title,
                  fontSize = 30.sp,
                  fontFamily = ChimpagneFontFamily,
                  modifier = Modifier.testTag("event title"))
            },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }, modifier = Modifier.testTag("go back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
              }
            })
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)) {
              LazyColumn {
                item {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier =
                                Modifier.horizontalScroll(rememberScrollState())
                                    .testTag("tag list")) {
                              uiState.tags.forEach { tag -> SimpleTagChip(tag) }
                            }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text =
                                uiState.guests.count().toString() +
                                    " " +
                                    stringResource(
                                        id = R.string.event_details_screen_number_of_guests),
                            fontSize = 24.sp,
                            fontFamily = ChimpagneFontFamily,
                            modifier = Modifier.testTag("number of guests"))
                        Spacer(Modifier.height(16.dp))
                        Legend(
                            text =
                                stringResource(id = R.string.date_tools_from) +
                                    " " +
                                    timestampToStringWithDateAndTime(
                                        buildTimestamp(uiState.startsAtCalendarDate)) +
                                    "\n " +
                                    stringResource(id = R.string.date_tools_until) +
                                    " " +
                                    timestampToStringWithDateAndTime(
                                        buildTimestamp(uiState.endsAtCalendarDate)),
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = "event date")
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = uiState.description,
                            fontSize = 20.sp,
                            fontFamily = ChimpagneFontFamily,
                            color = Color.LightGray,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .absolutePadding(left = 16.dp, right = 16.dp)
                                    .testTag("description"))
                        Spacer(Modifier.height(16.dp))
                        if (uiState.currentUserRole != ChimpagneRole.OWNER) {
                          ChimpagneButton(
                              text =
                                  stringResource(id = R.string.event_details_screen_leave_button),
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
                              text = stringResource(id = R.string.event_details_screen_edit_button),
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
                            text = stringResource(id = R.string.event_details_screen_chat_button),
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
                                stringResource(id = R.string.event_details_screen_location_button),
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
                                stringResource(id = R.string.event_details_screen_supplies_button),
                            icon = Icons.Rounded.Backpack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .testTag("supplies"),
                          onClick = {
                            navObject.navigateTo(Route.SUPPLIES + "/${uiState.id}")
                          })
                        Spacer(Modifier.height(16.dp))
                        ChimpagneButton(
                            text = stringResource(id = R.string.event_details_screen_voting_button),
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
