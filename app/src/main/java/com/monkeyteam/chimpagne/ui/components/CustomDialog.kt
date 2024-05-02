package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun CustomDialog(title: String, description: String, onDismissRequest: () -> Unit, buttonDataList: List<ButtonData> , content: @Composable () -> Unit) {
  Dialog(onDismissRequest = onDismissRequest) {
    Card {
      Column {
        Text(title)
        Text(description)
        content()
        Row {
          buttonDataList.forEach {
            TextButton(onClick = it.onClick) {
              Text(it.text)
            }
          }
        }
      }
    }
  }

}

data class ButtonData(
  val text: String,
  val onClick: () -> Unit
)