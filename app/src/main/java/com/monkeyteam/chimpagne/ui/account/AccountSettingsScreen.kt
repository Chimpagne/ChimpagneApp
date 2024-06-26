import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monkeyteam.chimpagne.R
import com.monkeyteam.chimpagne.ui.components.ChimpagneSpacer
import com.monkeyteam.chimpagne.ui.components.GoBackButton
import com.monkeyteam.chimpagne.ui.components.IconTextButton
import com.monkeyteam.chimpagne.ui.components.ProfileIcon
import com.monkeyteam.chimpagne.ui.components.TopBar
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    accountViewModel: AccountViewModel,
    onGoBack: () -> Unit,
    onEditRequest: () -> Unit,
    onLogout: () -> Unit,
) {
  val accountViewModelState by accountViewModel.uiState.collectAsState()
  var showDialog by remember { mutableStateOf(false) }
  if (showDialog) {
    DeleteAccountDialog(
        onConfirm = {
          showDialog = false
          accountViewModel.deleteAccount(
              onSuccess = onLogout,
              onFailure = { Log.e("AccountSettingsScreen", "Failed to delete account", it) })
        },
        onDismiss = { showDialog = false })
  }
  Scaffold(
      topBar = {
        TopBar(
            text = stringResource(id = R.string.account_settings_screen_title),
            navigationIcon = { GoBackButton(onClick = onGoBack) })
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = onEditRequest, modifier = Modifier.testTag("edit_account_button")) {
              Icon(Icons.Default.Edit, contentDescription = "Edit account")
            }
      }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              ChimpagneSpacer()

              ProfileIcon(
                  uri = accountViewModelState.currentUserProfilePicture,
                  onClick = {},
                  enabled = false,
                  size = 200.dp)

              ChimpagneSpacer()

              Row {
                SettingItem(
                    label = stringResource(id = R.string.account_first_name),
                    value = accountViewModelState.currentUserAccount?.firstName ?: "",
                    modifier = Modifier.testTag("account_settings_first_name"))
                ChimpagneSpacer()
                SettingItem(
                    label = stringResource(id = R.string.account_last_name),
                    value = accountViewModelState.currentUserAccount?.lastName ?: "",
                    modifier = Modifier.testTag("account_settings_last_name"))
              }

              ChimpagneSpacer()
              IconTextButton(
                  onClick = onLogout,
                  text = stringResource(id = R.string.account_logout),
                  icon = Icons.AutoMirrored.Default.Logout,
                  modifier = Modifier.testTag("account_settings_logout_button"))
              ChimpagneSpacer()
              IconTextButton(
                  text = stringResource(id = R.string.delete_account),
                  icon = Icons.Default.Delete,
                  color = MaterialTheme.colorScheme.error,
                  textColor = MaterialTheme.colorScheme.onError,
                  onClick = { showDialog = true },
                  modifier =
                      Modifier.testTag("account_settings_delete_button")
                          .padding(horizontal = 16.dp, vertical = 8.dp))
            }
      }
}

@Composable
fun SettingItem(label: String, value: String, modifier: Modifier) {
  Column(modifier = modifier.padding(vertical = 4.dp)) {
    Text(text = label, style = TextStyle(fontSize = 18.sp, fontFamily = ChimpagneFontFamily))
    Text(text = value, style = TextStyle(fontSize = 23.sp, fontFamily = ChimpagneFontFamily))
  }
}

@Composable
fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = {
        Text(
            text = stringResource(id = R.string.confirm_delete_title),
            modifier = Modifier.testTag("delete_account_dialog_title"))
      },
      text = {
        Text(
            text = stringResource(id = R.string.confirm_delete_message),
            modifier = Modifier.testTag("delete_account_dialog_message"))
      },
      confirmButton = {
        TextButton(
            onClick = onConfirm,
            modifier = Modifier.testTag("delete_account_dialog_confirm_button")) {
              Text(text = stringResource(id = R.string.chimpagne_confirm))
            }
      },
      dismissButton = {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("delete_account_dialog_cancel_button")) {
              Text(text = stringResource(id = R.string.chimpagne_cancel))
            }
      })
}
