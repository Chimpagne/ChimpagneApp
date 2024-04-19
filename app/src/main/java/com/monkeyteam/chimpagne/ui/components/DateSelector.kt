import android.app.Dialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.monkeyteam.chimpagne.model.utils.buildCalendar
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import java.text.DateFormat
import java.util.Calendar

/**
 * Example of a date picker dialog that can be used in the app.
 *
 * @param selectedDate The selected date to display in the date picker.
 * @param onDismissRequest Callback when the date picker is dismissed.
 * @param onDateSelected Callback when a date is selected.
 * @sample ChimpagneDatePicker( selectedDate, { showDatePicker = false }, { calendar -> selectedDate
 *   = calendar })
 */
@ExperimentalMaterial3Api
@Composable
fun DateSelector(
    selectedDate: Calendar,
    onDateSelected: (Calendar) -> Unit,
    modifier: Modifier = Modifier,
    selectTimeOfDay: Boolean = false
) {

  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  var day by remember { mutableIntStateOf(0) }
  var month by remember { mutableIntStateOf(0) }
  var year by remember { mutableIntStateOf(0) }

  val datePickerState =
      rememberDatePickerState(initialSelectedDateMillis = selectedDate.timeInMillis)

  val timePickerState =
      rememberTimePickerState(
          initialHour = selectedDate.get(Calendar.HOUR),
          initialMinute = selectedDate.get(Calendar.MINUTE))

  IconTextButton(
      text =
          DateFormat.getDateInstance(DateFormat.MEDIUM).format(selectedDate.time) +
              if (selectTimeOfDay) {
                " at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(selectedDate.time)
              } else {
                ""
              },
      icon = Icons.Rounded.CalendarToday,
      onClick = { showDatePicker = true },
      modifier = modifier)

  // Show date picker dialog when showDatePicker is true
  if (showDatePicker) {
    DatePickerDialog(
        onDismissRequest = { showDatePicker = false },
        confirmButton = {
          Button(
              onClick = {
                showDatePicker = false
                val dateToUse =
                    datePickerState.selectedDateMillis?.let {
                      Calendar.getInstance().apply { timeInMillis = it }
                    } ?: selectedDate
                if (selectTimeOfDay) {
                  showTimePicker = true
                  year = dateToUse.get(Calendar.YEAR)
                  month = dateToUse.get(Calendar.MONTH)
                  day = dateToUse.get(Calendar.DATE)
                } else {
                  onDateSelected(dateToUse)
                }
              }) {
                Text("OK")
              }
        }) {
          DatePicker(
              state = datePickerState,
          )
        }
  }

  if (showTimePicker) {
    TimePickerDialog(
        onDismissRequest = { showTimePicker = false },
        confirmButton = {
          TextButton(
              onClick = {
                showTimePicker = false

                val hour = timePickerState.hour
                val minute = timePickerState.minute

                onDateSelected(buildCalendar(day, month, year, hour, minute))
              }) {
                Text("OK")
              }
        }) {
          TimePicker(state = timePickerState)
        }
  }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
  Dialog(
      onDismissRequest = onDismissRequest,
      properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 6.dp,
        modifier =
            Modifier.width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(shape = MaterialTheme.shapes.extraLarge, color = containerColor),
        color = containerColor) {
          Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp).testTag("title"),
                    text = title,
                    style = MaterialTheme.typography.labelMedium)
                content()
                Row(modifier = Modifier.height(40.dp).fillMaxWidth()) {
                  Spacer(modifier = Modifier.weight(1f))
                  dismissButton?.invoke()
                  confirmButton()
                }
              }
        }
  }
}
