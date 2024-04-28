package com.monkeyteam.chimpagne.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.utilities.GoogleAuthentication

@Composable
fun LoginScreen(onSuccessfulLogin: (uid: String) -> Unit, onContinueAsGuest: () -> Unit) {
  val openAlertDialog = remember { mutableStateOf(false) }
  Column(
      modifier = Modifier.fillMaxSize().padding(15.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly) {
        Image(
            painter = painterResource(id = R.drawable.chimpagne_app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(250.dp).clip(CircleShape),
            contentScale = ContentScale.Fit)

        Column {
          Text(
              text = stringResource(id = R.string.welcome_screen_title),
              fontSize = 60.sp,
              modifier = Modifier.testTag("welcome_screen_title"))
          Text(text = "Chimpagne", fontSize = 60.sp, modifier = Modifier.testTag("Chimpagne"))
        }
        GoogleAuthentication(
            onSuccessfulLogin,
            { openAlertDialog.value = true },
            Modifier.fillMaxWidth(),
        )

        ChimpagneButton(
            text = "Continue As Guest",
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            onClick = onContinueAsGuest)
      }
  when {
    openAlertDialog.value -> {
      AlertDialog(
          icon = { Icon(Icons.Default.Info, contentDescription = "Example Icon") },
          title = { Text(text = "Sign in has failed") },
          text = { Text(text = "Sign in has failed, please try again another time") },
          onDismissRequest = { openAlertDialog.value = false },
          confirmButton = {
            TextButton(onClick = { openAlertDialog.value = false }) { Text("Confirm") }
          })
    }
  }
}
