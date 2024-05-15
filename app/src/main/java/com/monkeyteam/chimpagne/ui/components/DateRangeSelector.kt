package com.monkeyteam.chimpagne.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.model.utils.setCalendarToMidnight
import java.text.DateFormat
import java.util.Calendar

@Composable
fun DateRangeSelector(
    startDate: Calendar,
    endDate: Calendar,
    modifier: Modifier = Modifier,
    selectDateRange: (Calendar, Calendar) -> Unit
) {

  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    DateRangeSelectorDialog(
        startDate, endDate, onDismissRequest = { showDialog = false }, onSubmit = selectDateRange)
  }

  IconTextButton(
      text =
          "${DateFormat.getDateInstance(DateFormat.MEDIUM).format(startDate.time)} - ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(endDate.time)}",
      icon = Icons.Rounded.CalendarToday,
      onClick = { showDialog = true },
      modifier = modifier.testTag("date_range_button"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeSelectorDialog(
    startDate: Calendar,
    endDate: Calendar,
    onDismissRequest: () -> Unit,
    onSubmit: (Calendar, Calendar) -> Unit
) {
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

  CustomDialog(
      title = stringResource(id = R.string.select_date_range_for_query),
      description = "",
      onDismissRequest = onDismissRequest,
      buttonDataList =
          listOf(
              ButtonData(
                  stringResource(id = R.string.chimpagne_cancel), onClick = onDismissRequest),
              ButtonData(stringResource(id = R.string.chimpagne_ok), modifier = Modifier.testTag("date_range_submit")) {
                val newStartDate =
                    dateRangeState.selectedStartDateMillis?.let {
                      Calendar.getInstance().apply { timeInMillis = it }
                    } ?: Calendar.getInstance()

                val newEndDate =
                    dateRangeState.selectedEndDateMillis?.let {
                      Calendar.getInstance().apply { timeInMillis = it }
                    } ?: newStartDate

                onSubmit(newStartDate, newEndDate)
                onDismissRequest()
              })) {
        DateRangePicker(
            state = dateRangeState,
            showModeToggle = false,
            modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp.times(0.60f)))
      }
}
