package com.monkeyteam.chimpagne.ui.components.eventview

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backpack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.House
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PeopleAlt
import androidx.compose.material.icons.rounded.Poll
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneRole
import com.monkeyteam.chimpagne.ui.AccommodationsRow
import com.monkeyteam.chimpagne.ui.IconInfo
import com.monkeyteam.chimpagne.ui.IconRow
import com.monkeyteam.chimpagne.ui.components.SocialButtonRow
import com.monkeyteam.chimpagne.ui.components.areAllSocialMediaLinksEmpty
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.navigation.Route
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun EventActions(
    navObject: NavigationActions,
    eventViewModel: EventViewModel,
    isUserLoggedIn: Boolean,
    showToast: (String) -> Unit,
    showPromptLogin: (Boolean) -> Unit
) {

  val uiState by eventViewModel.uiState.collectAsState()
  val context = LocalContext.current

  Column {
    if (!areAllSocialMediaLinksEmpty(uiState.socialMediaLinks)) {
      ChimpagneLogoDivider(
          text = stringResource(id = R.string.event_details_screen_socials),
          icon = Icons.Rounded.People,
          modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp))
      SocialButtonRow(context = context, socialMediaLinks = uiState.socialMediaLinks)
    }
    ChimpagneLogoDivider(
        text = stringResource(id = R.string.event_details_screen_facilities),
        icon = Icons.Rounded.House,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp))

    AccommodationsRow(eventViewModel)

    ChimpagneLogoDivider(
        text = stringResource(id = R.string.event_details_screen_actions),
        icon = Icons.Rounded.TouchApp,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp))
    val iconList = mutableListOf<IconInfo>()
    if (uiState.currentUserRole == ChimpagneRole.OWNER) {
      iconList.add(
          IconInfo(
              icon = Icons.Rounded.Edit,
              description = stringResource(id = R.string.event_details_screen_edit_button),
              onClick = { navObject.navigateTo(Route.EDIT_EVENT_SCREEN + "/${uiState.id}") },
              testTag = "edit"))
      iconList.add(
          IconInfo(
              icon = Icons.Rounded.PeopleAlt,
              description = stringResource(id = R.string.event_details_screen_manage_staff_button),
              onClick = { navObject.navigateTo(Route.MANAGE_STAFF_SCREEN + "/${uiState.id}") },
              testTag = "manage staff"))
    } else {
      iconList.add(
          IconInfo(
              icon = Icons.Rounded.RemoveCircleOutline,
              description = stringResource(id = R.string.event_details_screen_leave_button),
              onClick = {
                if (isUserLoggedIn) {
                  eventViewModel.leaveEvent(
                      onSuccess = {
                        showToast(
                            context.getString(R.string.event_details_screen_leave_toast_success))
                        navObject.goBack()
                      },
                      onFailure = {Toast.makeText(context, context.getString(R.string.event_details_screen_leave_failure), Toast.LENGTH_SHORT).show()})
                } else {
                  showPromptLogin(true)
                }
              },
              testTag = "leave"))
    }
    iconList.addAll(
        listOf(
            IconInfo(
                icon = Icons.Rounded.Backpack,
                description = stringResource(id = R.string.event_details_screen_supplies_button),
                onClick = { navObject.navigateTo(Route.SUPPLIES_SCREEN + "/" + uiState.id) },
                testTag = "supplies"),
            IconInfo(
                icon = Icons.Rounded.Poll,
                description = stringResource(id = R.string.event_details_screen_voting_button),
                onClick = { navObject.navigateTo(Route.POLLS_SCREEN + "/" + uiState.id) },
                testTag = "polls")))
    IconRow(icons = iconList)
    Spacer(modifier = Modifier.padding(8.dp))
  }
}
