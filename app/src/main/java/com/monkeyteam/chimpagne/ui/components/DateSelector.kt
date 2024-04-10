import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
  selectedDate: Calendar, onDateSelected: (Calendar) -> Unit, modifier: Modifier = Modifier
) {

  var showDatePicker by remember { mutableStateOf(false) }

  val datePickerState =
    rememberDatePickerState(initialSelectedDateMillis = selectedDate.timeInMillis)

  IconTextButton(text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(selectedDate.time),
    icon = Icons.Rounded.CalendarToday,
    onClick = { showDatePicker = true },
    modifier = modifier)

// Show date picker dialog when showDatePicker is true
  if (showDatePicker) {
    DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
      Button(onClick = {
        showDatePicker = false
        val dateToUse = datePickerState.selectedDateMillis?.let {
          Calendar.getInstance().apply { timeInMillis = it }
        } ?: selectedDate
        onDateSelected(dateToUse)
      }) {
        Text("OK")
      }
    }) {
      DatePicker(
        state = datePickerState,
      )
    }
  }


}
