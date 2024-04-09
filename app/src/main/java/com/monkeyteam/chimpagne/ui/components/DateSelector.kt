import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
    onDismissRequest: () -> Unit,
    onDateSelected: (Calendar) -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = selectedDate.timeInMillis)

    DatePickerDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    val dateToUse =
                        datePickerState.selectedDateMillis?.let {
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