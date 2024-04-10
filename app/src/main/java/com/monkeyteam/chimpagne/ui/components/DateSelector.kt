import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.monkeyteam.chimpagne.R
import java.util.Calendar

/**
 * A date picker dialog to be used accross the app.
 *
 * @param selectedDate The selected date to display in the date picker.
 * @param onDismissAndAfterSelected Callback when the date picker is dismissed or the date has been
 *   choosen
 * @param onDateSelected Callback when a date is selected.
 * @sample ChimpagneDatePicker( selectedDate, { showDatePicker = false }, { calendar -> selectedDate
 *   = calendar })
 */
@ExperimentalMaterial3Api
@Composable
fun DateSelector(
    selectedDate: Calendar,
    onDismissAndAfterSelected: () -> Unit,
    onDateSelected: (Calendar) -> Unit
) {

  val datePickerState =
      rememberDatePickerState(initialSelectedDateMillis = selectedDate.timeInMillis)

  DatePickerDialog(
      onDismissRequest = { onDismissAndAfterSelected() },
      confirmButton = {
        Button(
            onClick = {
              val dateToUse =
                  datePickerState.selectedDateMillis?.let {
                    Calendar.getInstance().apply { timeInMillis = it }
                  } ?: selectedDate
              onDateSelected(dateToUse)
              onDismissAndAfterSelected()
            }) {
              Text(stringResource(R.string.validate_date_choice))
            }
      }) {
        DatePicker(state = datePickerState, showModeToggle = false)
      }
}
