package com.monkeyteam.chimpagne.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.utilities.GoogleAuthentication

@Composable
fun LoginScreen(successfulLogin: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
      Image(
          painter = painterResource(id = R.drawable.chimpagne_app_logo),
          contentDescription = "App Logo",
          modifier = Modifier.size(250.dp).padding(top = 62.dp).clip(CircleShape),
          contentScale = ContentScale.Fit
      )

      Text(
          text = "Welcome to",
          fontSize = 60.sp,
          modifier = Modifier.padding(top = 80.dp)
      )
      Text(
          text = "Chimpagne",
          fontSize = 60.sp,
          modifier = Modifier.padding(bottom = 62.dp)
      )

      Spacer(modifier = Modifier.height(130.dp))

      GoogleAuthentication(successfulLogin)
  }
}
