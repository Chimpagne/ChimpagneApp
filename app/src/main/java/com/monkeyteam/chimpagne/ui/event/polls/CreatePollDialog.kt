package com.monkeyteam.chimpagne.ui.event.polls

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.database.ChimpagnePoll
import com.monkeyteam.chimpagne.ui.components.ButtonData
import com.monkeyteam.chimpagne.ui.components.CustomDialog
import com.monkeyteam.chimpagne.viewmodels.EventViewModel

@Composable
fun CreatePollDialog(
    onPollCreate: (ChimpagnePoll) -> Unit,
    onPollCancel: () -> Unit,
    onDismissRequest: () -> Unit,
){
    var title by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    val options: MutableList<String> = mutableListOf("", "")

     CustomDialog(
         title = "Create A Poll",
         onDismissRequest = onDismissRequest,
         buttonDataList =
             listOf(
                 ButtonData(
                     text = stringResource(id = R.string.chimpagne_cancel),
                     modifier = Modifier.testTag("cancel_poll_button"),
                     onClick = onPollCancel
                 ),
                 ButtonData(
                     text = stringResource(id = R.string.chimpagne_confirm),
                     modifier = Modifier.testTag("confirm_poll_button"),
                     onClick = {
                         onPollCreate(
                             ChimpagnePoll(
                                 title = title,
                                 query = query,
                                 options = options
                             )
                        )
                     }
                 )
             )
     ){

     OutlinedTextField(
         modifier = Modifier
             .fillMaxWidth()
             .padding(5.dp)
             .testTag("poll_title_field"),
         value = title,
         onValueChange = { title = it },
         label = { Text("Poll title") }
     )
         OutlinedTextField(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(5.dp)
                 .testTag("poll_query_field"),
             value = query,
             onValueChange = { query = it },
             label = { Text("Poll query") }
         )
         LazyColumn {
             items(options.indices.toList()){id ->
                 var optionMutable by remember { mutableStateOf(options[id]) }
                 OutlinedTextField(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(5.dp)
                         .testTag("poll_options" + id + "_field"),
                     value = optionMutable,
                     onValueChange = {
                         optionMutable = it
                         options[id] = it
                                     },
                     trailingIcon = {
                         if(options.size > 2 && id == options.size - 1)
                         Icon(
                            Icons.Rounded.RemoveCircle,
                             "remove_option",
                             Modifier.clickable {
                                 options.removeLast()
                                 Log.d("JUAN TEST", options.toString())
                                 Log.d("JUAN TEST", options.size.toString())
                             }
                         )
                     }
                 )
             }
             item{
                 Row (
                     horizontalArrangement = Arrangement.Start
                 ){
                     if(options.size < 4){
                         Icon(
                             Icons.Rounded.AddCircle,
                             "add_option",
                             Modifier.clickable {
                                 options.add("")
                                 Log.d("JUAN TEST", options.toString())
                                 Log.d("JUAN TEST", options.size.toString())
                             }
                         )
                     }
                 }
             }
         }
     }
}
