package com.monkeyteam.chimpagne.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.utils.createCalendarIntent
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTypography
import java.util.Locale

@Composable
fun ChimpagneButton(
    modifier: Modifier = Modifier,
    text: String = "Click Me",
    icon: ImageVector? = null,
    textStyle: TextStyle = ChimpagneTypography.displayMedium,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(12.dp),
    padding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
  Button(
      onClick = onClick,
      modifier =
          modifier
              .wrapContentWidth()
              .padding(horizontal = 24.dp)
              .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp)),
      colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
      shape = shape,
      contentPadding = padding) {
        if (icon != null) {
          Icon(imageVector = icon, contentDescription = "Button icon desc")
          Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontFamily = ChimpagneFontFamily,
            style = textStyle,
            textAlign = TextAlign.Center)
      }
}

@SuppressLint("ModifierParameter")
@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          modifier
              .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp))
              .background(shape = RoundedCornerShape(12.dp), color = color)
              .clickable(onClick = onClick)
              .padding(horizontal = 24.dp, vertical = 12.dp)) {
        Icon(icon, contentDescription = text)
        Spacer(Modifier.width(8.dp))
        Text(text.uppercase(Locale.ROOT))
      }
}

@Composable
fun GoBackButton(navigationActions: NavigationActions) {
  IconButton(onClick = { navigationActions.goBack() }) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Go Back",
        tint = MaterialTheme.colorScheme.onSurface)
  }
}

@Composable
fun CalendarButton(event: ChimpagneEvent?, contextMainActivity: Context) {
  var showDialog by remember { mutableStateOf(false) }
  Box(
      modifier =
          Modifier.padding(8.dp)
              .shadow(10.dp, RoundedCornerShape(12.dp))
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .clickable { showDialog = true }
              .padding(8.dp)
              .testTag("calendarButton")) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Add to Calendar",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(36.dp).testTag("calendarButtonIcon"))
      }
  if (showDialog) {
    popUpCalendar(
        onAccept = {
          val intent = createCalendarIntent(event)
          if (intent != null) {
            contextMainActivity.startActivity(intent)
          } else {
            Toast.makeText(
                    contextMainActivity, "Event can't be added to calendar", Toast.LENGTH_SHORT)
                .show()
          }
          showDialog = false
        },
        onReject = { showDialog = false },
        event = event)
  }
}

@Composable
fun popUpCalendar(onAccept: () -> Unit, onReject: () -> Unit, event: ChimpagneEvent?) {
  val textQuestion =
      stringResource(id = R.string.add_event_to_calendar_prefix) +
          event?.title +
          stringResource(id = R.string.add_event_to_calendar_suffix)
  val builder =
      AlertDialog(
          onDismissRequest = { onReject() },
          title = { Text("Add to Calendar") },
          text = { Text(textQuestion) },
          confirmButton = {
            Button(onClick = { onAccept() }, modifier = Modifier.testTag("acceptButton")) {
              Text("Yes")
            }
          },
          dismissButton = {
            Button(onClick = { onReject() }, modifier = Modifier.testTag("rejectButton")) {
              Text("No")
            }
          })
}

@Composable
fun SocialButton(imageLogo: Int, urlAsString: String, context: Context, testTag: String) {
  Image(
      painter = painterResource(id = imageLogo),
      contentDescription = "Social Button",
      modifier =
          Modifier.size(55.dp)
              .clickable {
                val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAsString))
                context.startActivity(urlIntent)
              }
              .testTag(testTag))
}

@Composable
fun SocialButtonRow(context: Context, socialMediaLinks: Map<String, SocialMedia>) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        for (socialMedia in socialMediaLinks.values) {
          if (socialMedia.chosenGroupUrl.isNotEmpty()) {
            SocialButton(
                imageLogo = socialMedia.iconResource,
                urlAsString = socialMedia.chosenGroupUrl,
                context = context,
                testTag = socialMedia.testTag)
          }
        }
      }
}

@Composable
fun ReportProblemButton(onClick: () -> Unit) {
  Button(
      onClick = onClick,
      modifier =
          Modifier.testTag("reportProblem")
              .shadow(10.dp, RoundedCornerShape(12.dp))
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primaryContainer),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.event_details_screen_report_problem),
            fontFamily = ChimpagneFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 16.sp)
      }
}
