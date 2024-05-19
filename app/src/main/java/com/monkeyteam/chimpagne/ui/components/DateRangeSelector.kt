package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.utils.setCalendarToMidnight
import java.text.DateFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    startDate: Calendar,
    endDate: Calendar,
    modifier: Modifier = Modifier,
    selectDateRange: (Calendar, Calendar) -> Unit
) {

  var showBottomSheet by remember { mutableStateOf(false) }

  val dateRangeState =
    rememberDateRangePickerState(
      initialSelectedStartDateMillis = startDate.timeInMillis,
      initialSelectedEndDateMillis = endDate.timeInMillis,
      selectableDates =
      object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
          val today = Calendar.getInstance()
          setCalendarToMidnight(today)

          return utcTimeMillis >= today.timeInMillis
        }
      })

  if (showBottomSheet) {
    ModalBottomSheet(
      onDismissRequest = {
        val newStartDate =
          dateRangeState.selectedStartDateMillis?.let {
            Calendar.getInstance().apply { timeInMillis = it }
          } ?: Calendar.getInstance()

        val newEndDate =
          dateRangeState.selectedEndDateMillis?.let {
            Calendar.getInstance().apply { timeInMillis = it }
          } ?: newStartDate

        selectDateRange(newStartDate, newEndDate)
        showBottomSheet = false
      }, modifier = Modifier.testTag("date_range_submit")) {
      DateRangePicker(state = dateRangeState)
    }
  }

  IconTextButton(
      text =
          "${
      DateFormat.getDateInstance(DateFormat.MEDIUM).format(startDate.time)
    } - ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(endDate.time)}",
      icon = Icons.Rounded.CalendarToday,
      onClick = { showBottomSheet = true },
      modifier = modifier.testTag("date_range_button"))
}
