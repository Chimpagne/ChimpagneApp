package com.monkeyteam.chimpagne.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.model.database.ChimpagneEvent
import com.monkeyteam.chimpagne.model.intents.CalendarIntents
import com.monkeyteam.chimpagne.ui.navigation.NavigationActions
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import java.util.Locale

@Composable
fun ChimpagneButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(12.dp),
    padding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
  Button(
      onClick = onClick,
      modifier = modifier.wrapContentWidth().padding(horizontal = 24.dp),
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
            fontWeight = fontWeight,
            fontSize = fontSize,
            lineHeight = fontSize,
            textAlign = TextAlign.Center)
      }
}

@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          modifier
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(100))
              .background(
                  shape = RoundedCornerShape(100), color = MaterialTheme.colorScheme.surfaceVariant)
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
  IconButton(modifier = Modifier.testTag("calendarButton"), onClick = { showDialog = true }) {
    Icon(
        imageVector = Icons.Default.CalendarMonth,
        contentDescription = "Add to Calendar",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("calendarButtonIcon"))
  }
  if (showDialog) {
    popUpCalendar(
        onAccept = {
          val intent = CalendarIntents().addToCalendar(event)
          if (intent != null) {
            contextMainActivity.startActivity(intent)
          } else {
            Toast.makeText(
                    contextMainActivity, "Event can't be added to calendar", Toast.LENGTH_SHORT)
                .show()
          }
          showDialog = false // Close the dialog after handling
        },
        onReject = {
          showDialog = false // Close the dialog when rejected
        },
        context = contextMainActivity,
        event = event)
  }
}

@Composable
fun popUpCalendar(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    context: Context,
    event: ChimpagneEvent?
) {
  val builder =
      AlertDialog(
          onDismissRequest = { onReject() },
          title = { Text("Add to Calendar") },
          text = { Text("Do you want to add the event \"${event?.title}\" to your calendar?") },
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
