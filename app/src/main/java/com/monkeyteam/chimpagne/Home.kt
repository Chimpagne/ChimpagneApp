package com.monkeyteam.chimpagne

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.ui.components.ChimpagneButton
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.ui.theme.ChimpagneTheme

@Composable
fun HomeScreen() {
  ChimpagneTheme {
    Column(
        modifier =
            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          ChimpagneButton(
              onClick = { /*TODO*/},
              text = {
                Text(
                    text = "MY PARTIES",
                    fontFamily = ChimpagneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp)
              })
          Spacer(modifier = Modifier.height(16.dp))
          ChimpagneButton(
              onClick = { /*TODO*/},
              text = {
                Text(
                    text = "JOIN A PARTY",
                    fontFamily = ChimpagneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp)
              })
          Spacer(modifier = Modifier.height(16.dp))
          ChimpagneButton(
              onClick = { /*TODO*/},
              text = {
                Text(
                    text = "ORGANIZE A PARTY",
                    fontFamily = ChimpagneFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp)
              })
        }
  }
}

@Preview
@Composable
fun PreviewHomeScreen() {
  ChimpagneTheme { HomeScreen() }
}
