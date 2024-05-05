package com.monkeyteam.chimpagne.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.GroupRemove
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.ui.components.Legend
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@ExperimentalMaterial3Api
@Composable
fun ManageStaffScreen(navObject: NavigationActions, eventViewModel: EventViewModel) {
  val uiState by eventViewModel.uiState.collectAsState()
  var isOnEdit by remember { mutableStateOf(false) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Manage Staff", Modifier.testTag("screen title")) },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = {
              IconButton(
                  onClick = {
                    navObject.goBack()
                  }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                  }
            })
      },
      floatingActionButton = {
          FloatingActionButton(
              modifier = Modifier.size(70.dp),
              onClick = {
              if (isOnEdit) eventViewModel.updateTheEvent({ isOnEdit = false })
              else isOnEdit = true
          }){
              Icon(
                  if (isOnEdit) Icons.Rounded.Save
                  else Icons.Rounded.Edit, "floating button"
              )
          }
      }) { innerPadding ->
      Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start) {
              Spacer(Modifier.height(16.dp))
              LazyColumn {
                item {
                  Legend(
                      text = "Staff List:",
                      imageVector =
                          if (isOnEdit) Icons.Rounded.GroupRemove else Icons.Rounded.Group,
                      contentDescription = "Staff List")
                }
                if (uiState.staffs.isEmpty()) {
                  item {
                    Text(
                        text = "There are no staff for this event",
                        modifier = Modifier.padding(16.dp).testTag("empty staff list"))
                  }
                } else {
                  items(uiState.staffs.keys.toList().sorted()) { uid ->
                      if(uiState.accounts[uid] != null) {
                          val account = uiState.accounts[uid]!!
                          PersonCard(
                              firstName = account.firstName,
                              lastName = account.lastName,
                              isOnEdit = isOnEdit,
                              isStaff = true,
                              modifier = Modifier.testTag("staff member")
                          ) {
                              eventViewModel.demoteStaffToGuest(account.firebaseAuthUID)
                          }
                      }
                  }
                }
                if (isOnEdit) {
                  item {
                    Legend(
                        text = "Guest List:",
                        imageVector = Icons.Rounded.GroupAdd,
                        contentDescription = "Guest List")
                  }
                  if (uiState.guests.isEmpty()) {
                    item {
                      Text(
                          text = "There are no guests for this event",
                          modifier = Modifier.padding(16.dp).testTag("empty guest list"))
                    }
                  } else {
                    items(uiState.guests.keys.toList().sorted()) { uid ->
                        if(uiState.accounts[uid] != null){
                            val account = uiState.accounts[uid]!!
                            PersonCard(
                                firstName = account.firstName,
                                lastName = account.lastName,
                                isOnEdit = true,
                                isStaff = false,
                                modifier = Modifier.testTag("guest member")
                            ) {
                                eventViewModel.promoteGuestToStaff(account.firebaseAuthUID)
                            }
                        }

                    }
                  }
                }
              }
            }
      }
}

@Composable
fun PersonCard(
    firstName: String,
    lastName: String,
    isStaff: Boolean,
    modifier: Modifier = Modifier,
    isOnEdit: Boolean = false,
    onClickForToggle: () -> Unit = {}
) {
  Card(
      modifier = modifier.clickable { if(isOnEdit){onClickForToggle()} }.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
          Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = firstName,
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lastName,
                fontFamily = ChimpagneFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center)
          }
          Spacer(Modifier.weight(1f))
          if (isOnEdit) {
            Icon(
                modifier = Modifier.size(55.dp).padding(10.dp),
                imageVector =
                    if (isStaff) Icons.Rounded.RadioButtonChecked
                    else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = "Staff / Guest")
          }
        }
      }
}
