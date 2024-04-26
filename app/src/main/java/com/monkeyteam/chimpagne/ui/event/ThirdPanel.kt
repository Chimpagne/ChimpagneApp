package com.monkeyteam.chimpagne.ui.event

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun ThirdPanel(eventViewModel: EventViewModel) {
  val context = LocalContext.current
  Column(modifier = Modifier.padding(16.dp)) {
    Text(
        stringResource(id = R.string.event_creation_screen_groceries),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.testTag("groceries_title"))
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = {
          Toast.makeText(
                  context,
                  context.getString(R.string.event_creation_screen_gorceries_toast),
                  Toast.LENGTH_SHORT)
              .show()
        },
        modifier = Modifier.testTag("add_groceries_button")) {
          Text(stringResource(id = R.string.event_creation_screen_add_groceries))
        }
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn {
      // Populate with groceries items
    }
  }
}
