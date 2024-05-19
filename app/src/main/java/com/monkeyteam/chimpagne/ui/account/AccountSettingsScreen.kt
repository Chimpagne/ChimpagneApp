import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.monkeyteam.chimpagne.ui.theme.ChimpagneFontFamily
import com.monkeyteam.chimpagne.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    accountViewModel: AccountViewModel,
    onGoBack: () -> Unit,
    onEditRequest: () -> Unit,
    onLogout: () -> Unit
) {
  val accountViewModelState by accountViewModel.uiState.collectAsState()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(stringResource(id = R.string.account_settings_screen_title)) },
            modifier = Modifier.shadow(4.dp),
            navigationIcon = { GoBackButton(onClick = onGoBack) },
        )
      },
      floatingActionButton = {
        FloatingActionButton(onClick = onEditRequest, modifier = Modifier.testTag("edit_account")) {
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
                    label = "First Name",
                    value = accountViewModelState.currentUserAccount?.firstName ?: "",
                    modifier = Modifier.testTag("firstNameTextField"))
                ChimpagneSpacer()
                SettingItem(
                    label = "Last Name",
                    value = accountViewModelState.currentUserAccount?.lastName ?: "",
                    modifier = Modifier.testTag("lastNameTextField"))
              }

              ChimpagneSpacer()
              IconTextButton(
                  onClick = onLogout,
                  text = stringResource(id = R.string.account_logout),
                  icon = Icons.AutoMirrored.Default.Logout)
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
