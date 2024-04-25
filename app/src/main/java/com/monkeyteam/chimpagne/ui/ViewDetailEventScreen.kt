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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.monkeyteam.chimpagne.model.utils.buildTimestamp
import com.monkeyteam.chimpagne.model.utils.timestampToStringWithDateAndTime
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.components.SimpleTagChip
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.EventViewModel
import java.text.DateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDetailEventScreen(
    navObject: NavigationActions,
    eventViewModel: EventViewModel = viewModel(),
    canEditEvent: Boolean = false
) {
  val uiState by eventViewModel.uiState.collectAsState()
  val context = LocalContext.current
  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = uiState.title,
                  fontSize = 30.sp,
                  fontFamily = ChimpagneFontFamily)
            },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(onClick = { navObject.goBack() }) {
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
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            uiState.tags.forEach { tag -> SimpleTagChip(tag) }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = uiState.guests.count().toString() + " Monkeys joined",
                            fontSize = 24.sp,
                            fontFamily = ChimpagneFontFamily)
                        Spacer(Modifier.height(16.dp))
                        Legend(
                            text = " From " +
                                    timestampToStringWithDateAndTime(buildTimestamp(uiState.startsAtCalendarDate))
                                    + "\n until " +
                                    timestampToStringWithDateAndTime(buildTimestamp(uiState.endsAtCalendarDate)),
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
                                    .absolutePadding(left = 16.dp, right = 16.dp))
                        Spacer(Modifier.height(16.dp))
                          if (!canEditEvent) {
                              ChimpagneButton(
                                  text = "Leave this event",
                                  icon = Icons.Rounded.RemoveCircleOutline,
                                  fontWeight = FontWeight.Bold,
                                  fontSize = 30.sp,
                                  modifier =
                                  Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                      .fillMaxWidth(),
                                  onClick = {
                                      //TODO LEAVE DOES WORk BUT THE MY EVENTS ONLY UPDATES AFTER RE ENTRY TO IT
                                        eventViewModel.leaveTheEvent(
                                            onSuccess = {
                                                Toast.makeText(
                                                    context,
                                                    "Leaving was successful",
                                                    Toast.LENGTH_SHORT)
                                                    .show()
                                                //navObject.goBack()
                                            })
                                  })
                          }
                        if (canEditEvent) {
                          ChimpagneButton(
                              text = "Edit this event",
                              icon = Icons.Rounded.Edit,
                              fontWeight = FontWeight.Bold,
                              fontSize = 30.sp,
                              modifier =
                                  Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                      .fillMaxWidth(),
                              onClick = {
                                  /* TODO Implement this later */
                                  Toast.makeText(
                                      context,
                                      "This function will be implemented in a future version",
                                      Toast.LENGTH_SHORT)
                                      .show()
                              })
                        }
                        Spacer(Modifier.height(16.dp))
                        ChimpagneButton(
                            text = "Chat",
                            icon = Icons.Rounded.ChatBubbleOutline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
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
                            text = "Location",
                            icon = Icons.Rounded.LocationOn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
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
                            text = "Supplies",
                            icon = Icons.Rounded.Backpack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
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
                            text = "Polls and voting",
                            icon = Icons.Rounded.Poll,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
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
                            text = "Car pooling",
                            icon = Icons.Rounded.DirectionsCar,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            modifier =
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
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
