package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CustomDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    buttonDataList: List<ButtonData>,
    content: @Composable () -> Unit = {}
) {
  Dialog(onDismissRequest = onDismissRequest) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = AlertDialogDefaults.shape,
    ) {
      Column(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                title,
                modifier = Modifier.fillMaxWidth(),
                style =
                    TextStyle(
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight(400),
                        color = AlertDialogDefaults.titleContentColor,
                    ))
            Text(
                description,
                modifier = Modifier.fillMaxWidth().padding(0.dp, 7.dp),
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(400),
                        color = AlertDialogDefaults.textContentColor,
                        letterSpacing = 0.25.sp,
                    ))
            content()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
              buttonDataList.forEach { TextButton(onClick = it.onClick) { Text(it.text) } }
            }
          }
    }
  }
}

data class ButtonData(val text: String, val onClick: () -> Unit)
