package com.monkeyteam.chimpagne.ui.components

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import java.util.Locale

@Composable
fun TagField(selectedTags: List<String>, updateSelectedTags: (List<String>) -> Unit, onSearchToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {

  val keyboardController = LocalSoftwareKeyboardController.current
  val view = LocalView.current

  var tagInput by remember { mutableStateOf("") }


  val tagPredictions =
    remember(tagInput) {
      if (tagInput.isEmpty()) {
        listOf() // Return an empty list if addressInput is empty
      } else {
        listOf(
          "student",
          "disco",
          "vegan",
          "dark",
          "alcool",
          "beer",
          "bbq",
          "soundboks",
          "carnaval")
          .filter { it.contains(tagInput, ignoreCase = true) }
          .take(5)
      }
    }

  Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
    selectedTags.forEach { tag -> TagChip(tag = tag) { updateSelectedTags(selectedTags - tag) } }
  }

  Spacer(Modifier.height(16.dp))

  AutoCompleteTextView(
    modifier = modifier,
    query = tagInput,
    queryLabel = stringResource(id = R.string.find_event_search_tag_query_label),
    onQueryChanged = { updatedTag ->
      tagInput = updatedTag
      // Update addressPlaceItemPredictions based on the tagInput here
    },
    predictions = tagPredictions,
    onClearClick = {
      Log.d(tagInput, "tagInput")
      if (tagInput.isEmpty()) {
        keyboardController?.hide()
        view.clearFocus()
      } else {
        tagInput = ""
      }
    },
    onDoneActionClick = {
      if (tagInput.trim().isNotBlank()) {
        updateSelectedTags(selectedTags + tagInput.trim().lowercase(Locale.getDefault()))
        tagInput = ""
      } else {
        view.clearFocus()
      }
    },
    onItemClick = { selectedTag ->
      if (selectedTag.trim().isNotBlank()) {
        updateSelectedTags(selectedTags + selectedTag.trim().lowercase(Locale.getDefault()))
        tagInput = ""
      }
    },
    onFocusChanged = onSearchToggle) { tag ->
    // Define how the items need to be displayed
    Text(tag, fontSize = 14.sp)
  }
}